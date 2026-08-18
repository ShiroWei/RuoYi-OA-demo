package com.ruoyi.oa.approval.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.oa.approval.domain.OaApprovalApply;
import com.ruoyi.oa.approval.domain.OaApprovalFlow;
import com.ruoyi.oa.approval.mapper.OaApprovalApplyMapper;
import com.ruoyi.oa.approval.mapper.OaApprovalFlowMapper;
import com.ruoyi.oa.approval.service.IOaApprovalService;

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

    /**
     * 查询审批申请列表
     */
    @Override
    public List<OaApprovalApply> selectApprovalList(String type, Long userId)
    {
        OaApprovalApply query = new OaApprovalApply();
        if ("apply".equals(type))
        {
            query.setApplicantId(userId);
        }
        // todo / done 为审批人视角，工作流引擎接入后按审批人查询
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
     * 发起审批申请
     */
    @Override
    public int insertApproval(OaApprovalApply apply)
    {
        apply.setStatus("0");
        apply.setCurrentNode("部门审批");
        apply.setApplyTime(DateUtils.getNowDate());
        return approvalApplyMapper.insertOaApprovalApply(apply);
    }

    /**
     * 审批通过
     */
    @Override
    public int approve(Long applyId, Long userId, String comment)
    {
        OaApprovalApply apply = approvalApplyMapper.selectOaApprovalApplyById(applyId);
        if (apply == null)
        {
            return 0;
        }
        apply.setStatus("1");
        apply.setCurrentNode("审批完成");
        return approvalApplyMapper.updateOaApprovalApply(apply);
    }

    /**
     * 审批驳回
     */
    @Override
    public int reject(Long applyId, Long userId, String comment)
    {
        OaApprovalApply apply = approvalApplyMapper.selectOaApprovalApplyById(applyId);
        if (apply == null)
        {
            return 0;
        }
        apply.setStatus("2");
        apply.setCurrentNode("已驳回");
        return approvalApplyMapper.updateOaApprovalApply(apply);
    }
}