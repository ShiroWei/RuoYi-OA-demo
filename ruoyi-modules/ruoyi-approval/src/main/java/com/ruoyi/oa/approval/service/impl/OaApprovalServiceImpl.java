package com.ruoyi.oa.approval.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.oa.api.RemoteOaTodoService;
import com.ruoyi.oa.approval.domain.OaApprovalApply;
import com.ruoyi.oa.approval.domain.OaApprovalFlow;
import com.ruoyi.oa.approval.mapper.OaApprovalApplyMapper;
import com.ruoyi.oa.approval.mapper.OaApprovalFlowMapper;
import com.ruoyi.oa.approval.service.IOaApprovalService;
import com.ruoyi.oa.todo.domain.OaTodoItem;

/**
 * 审批中心Service业务层处理
 * 
 * @author oa
 */
@Service
public class OaApprovalServiceImpl implements IOaApprovalService
{
    @Autowired
    private OaApprovalApplyMapper approvalApplyMapper;

    @Autowired
    private OaApprovalFlowMapper approvalFlowMapper;

    @Autowired
    private RemoteOaTodoService remoteTodoService;

    /**
     * 查询审批申请列表（type: todo 待我审批 / done 我已审批 / apply 我发起的）
     */
    @Override
    public List<OaApprovalApply> selectApprovalList(String type, Long userId)
    {
        OaApprovalApply query = new OaApprovalApply();
        if ("todo".equals(type))
        {
            query.setStatus("0");
            if (!Long.valueOf(1L).equals(userId))
            {
                return java.util.Collections.emptyList();
            }
        }
        else if ("done".equals(type))
        {
            query.setStatuses(new String[] { "1", "2" });
        }
        else if ("apply".equals(type))
        {
            query.setApplicantId(userId);
        }
        return approvalApplyMapper.selectOaApprovalApplyList(query);
    }

    /**
     * 查询审批申请详细
     */
    @Override
    public OaApprovalApply selectApprovalById(Long applyId)
    {
        return approvalApplyMapper.selectOaApprovalApplyById(applyId);
    }

    /**
     * 查询申请流程时间线
     */
    @Override
    public List<OaApprovalFlow> selectApprovalFlow(Long applyId)
    {
        return approvalFlowMapper.selectFlowByApplyId(applyId);
    }

    /**
     * 发起审批申请：生成单号、初始化流程节点、Feign 联动待办服务生成待办
     */
    @Override
    @Transactional
    public int insertApproval(OaApprovalApply apply)
    {
        if (apply == null || apply.getTitle() == null || apply.getTitle().trim().isEmpty()
                || apply.getTitle().length() > 200 || apply.getContent() == null
                || apply.getContent().trim().isEmpty() || apply.getContent().length() > 1000)
        {
            throw new ServiceException("请填写有效的申请标题和事由");
        }
        String type = apply.getApplyType();
        if (!"请假".equals(type) && !"报销".equals(type) && !"出差".equals(type))
        {
            throw new ServiceException("不支持的申请类型");
        }
        if ("报销".equals(type))
        {
            if (apply.getAmount() == null || apply.getAmount().signum() <= 0
                    || apply.getAmount().compareTo(new java.math.BigDecimal("99999999.99")) > 0
                    || apply.getAmount().stripTrailingZeros().scale() > 2)
            {
                throw new ServiceException("报销金额必须大于零，最多两位小数且不超过99999999.99");
            }
            apply.setStartDate(null);
            apply.setEndDate(null);
            apply.setDays(null);
        }
        else
        {
            if (apply.getStartDate() == null || apply.getEndDate() == null
                    || apply.getEndDate().before(apply.getStartDate()))
            {
                throw new ServiceException("请填写有效的起止日期，结束日期不能早于开始日期");
            }
            java.time.ZoneId zone = java.time.ZoneId.systemDefault();
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    apply.getStartDate().toInstant().atZone(zone).toLocalDate(),
                    apply.getEndDate().toInstant().atZone(zone).toLocalDate()) + 1;
            if (days > Integer.MAX_VALUE)
            {
                throw new ServiceException("申请日期范围过大");
            }
            apply.setDays((int) days);
            apply.setAmount(null);
        }
        apply.setApplyNo(generateApplyNo());
        apply.setStatus("0");
        apply.setCurrentNode("部门审批");
        apply.setApplyTime(DateUtils.getNowDate());
        int result = approvalApplyMapper.insertOaApprovalApply(apply);

        // 初始化流程节点：提交 -> 部门审批 -> 人事审批 -> 审批完成
        insertFlowNode(apply.getApplyId(), "提交申请", 1, apply.getApplicant(), "finish", "提交" + apply.getApplyType() + "申请", DateUtils.getNowDate());
        insertFlowNode(apply.getApplyId(), "部门审批", 2, "", "process", "等待部门主管审批", null);
        insertFlowNode(apply.getApplyId(), "人事审批", 3, "", "wait", "等待人事部门处理", null);
        insertFlowNode(apply.getApplyId(), "审批完成", 4, "", "wait", "", null);

        // 跨服务联动待办服务生成待办（演示环境审批人固定为管理员）
        OaTodoItem todo = new OaTodoItem();
        todo.setBizType("approval");
        todo.setBizId(apply.getApplyId());
        todo.setTitle(apply.getApplicant() + "提交的" + apply.getApplyType() + "申请");
        todo.setTodoType(apply.getApplyType());
        todo.setSubmitter(apply.getApplicant());
        todo.setHandlerId(1L);
        todo.setPriority("中");
        todo.setStatus("0");
        todo.setSubmitTime(DateUtils.getNowDate());
        R<Boolean> remote = remoteTodoService.createTodo(todo);
        if (remote == null || R.isError(remote) || !Boolean.TRUE.equals(remote.getData()))
        {
            throw new ServiceException("待办生成失败，请重试");
        }
        return result;
    }

    /**
     * 审批通过：流转当前节点到下一环节，Feign 联动完成待办
     */
    @Override
    @Transactional
    public int approve(Long applyId, Long userId, String comment)
    {
        OaApprovalApply apply = approvalApplyMapper.selectOaApprovalApplyById(applyId);
        if (apply == null)
        {
            return 0;
        }
        if (!"0".equals(apply.getStatus()) || !Long.valueOf(1L).equals(userId))
        {
            throw new ServiceException("无权处理该审批或审批已结束");
        }
        List<OaApprovalFlow> flows = approvalFlowMapper.selectFlowByApplyId(applyId);
        // 当前处理节点标记完成
        for (OaApprovalFlow flow : flows)
        {
            if ("process".equals(flow.getStatus()))
            {
                flow.setStatus("finish");
                flow.setHandler(SecurityUtils.getUsername());
                flow.setHandleTime(DateUtils.getNowDate());
                flow.setComment(comment);
                approvalFlowMapper.updateOaApprovalFlow(flow);
                break;
            }
        }
        // 下一业务处理节点标记为处理中（审批完成为终结节点，不参与流转）
        boolean hasNext = false;
        for (OaApprovalFlow flow : flows)
        {
            if ("wait".equals(flow.getStatus()) && !"审批完成".equals(flow.getNodeName()))
            {
                flow.setStatus("process");
                approvalFlowMapper.updateOaApprovalFlow(flow);
                apply.setCurrentNode(flow.getNodeName());
                hasNext = true;
                break;
            }
        }
        if (!hasNext)
        {
            // 所有业务节点已完成，终结流程
            for (OaApprovalFlow flow : flows)
            {
                if ("审批完成".equals(flow.getNodeName()) && !"finish".equals(flow.getStatus()))
                {
                    flow.setStatus("finish");
                    approvalFlowMapper.updateOaApprovalFlow(flow);
                }
            }
            apply.setStatus("1");
            apply.setCurrentNode("审批完成");
        }
        int result = approvalApplyMapper.updateOaApprovalApply(apply);
        if ("1".equals(apply.getStatus()))
        {
            // 仅终态完成待办，避免第一次通过就丢失唯一待办。
            completeTodoByApplyId(applyId);
        }
        return result;
    }

    /**
     * 审批驳回：当前节点标记完成、申请置为驳回、Feign 联动完成待办
     */
    @Override
    @Transactional
    public int reject(Long applyId, Long userId, String comment)
    {
        OaApprovalApply apply = approvalApplyMapper.selectOaApprovalApplyById(applyId);
        if (apply == null)
        {
            return 0;
        }
        if (!"0".equals(apply.getStatus()) || !Long.valueOf(1L).equals(userId))
        {
            throw new ServiceException("无权处理该审批或审批已结束");
        }
        List<OaApprovalFlow> flows = approvalFlowMapper.selectFlowByApplyId(applyId);
        for (OaApprovalFlow flow : flows)
        {
            if ("process".equals(flow.getStatus()))
            {
                flow.setStatus("finish");
                flow.setHandler(SecurityUtils.getUsername());
                flow.setHandleTime(DateUtils.getNowDate());
                flow.setComment(comment);
                approvalFlowMapper.updateOaApprovalFlow(flow);
                break;
            }
        }
        apply.setStatus("2");
        apply.setCurrentNode("已驳回");
        int result = approvalApplyMapper.updateOaApprovalApply(apply);
        // 跨服务联动待办服务完成待办
        completeTodoByApplyId(applyId);
        return result;
    }

    /**
     * 生成申请单号：A + yyyyMMddHHmmss
     */
    private String generateApplyNo()
    {
        return "A" + DateUtils.parseDateToStr("yyyyMMddHHmmss", DateUtils.getNowDate());
    }

    /**
     * 插入流程节点
     */
    private void insertFlowNode(Long applyId, String nodeName, Integer nodeOrder, String handler, String status, String comment, java.util.Date handleTime)
    {
        OaApprovalFlow flow = new OaApprovalFlow();
        flow.setApplyId(applyId);
        flow.setNodeName(nodeName);
        flow.setNodeOrder(nodeOrder);
        flow.setHandler(handler);
        flow.setStatus(status);
        flow.setComment(comment);
        flow.setHandleTime(handleTime);
        approvalFlowMapper.insertOaApprovalFlow(flow);
    }

    /**
     * 按申请ID Feign 联动待办服务完成待办
     */
    private void completeTodoByApplyId(Long applyId)
    {
        R<Boolean> remote = remoteTodoService.completeTodoByApplyId(applyId);
        if (remote == null || R.isError(remote) || !Boolean.TRUE.equals(remote.getData()))
        {
            throw new ServiceException("完成待办失败");
        }
    }
}
