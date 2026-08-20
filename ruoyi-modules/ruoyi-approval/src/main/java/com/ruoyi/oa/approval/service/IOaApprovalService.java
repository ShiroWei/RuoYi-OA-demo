package com.ruoyi.oa.approval.service;

import java.util.List;
import com.ruoyi.oa.approval.domain.OaApprovalApply;
import com.ruoyi.oa.approval.domain.OaApprovalFlow;

/**
 * 审批中心Service接口
 * 
 * @author oa
 */
public interface IOaApprovalService
{
    /**
     * 查询审批申请列表（type: todo 待我审批 / done 我已审批 / apply 我发起的）
     */
    public List<OaApprovalApply> selectApprovalList(String type, Long userId);

    /**
     * 查询审批申请详细
     */
    public OaApprovalApply selectApprovalById(Long applyId);

    /**
     * 查询申请流程时间线
     */
    public List<OaApprovalFlow> selectApprovalFlow(Long applyId);

    /**
     * 发起审批申请
     */
    public int insertApproval(OaApprovalApply apply);

    /**
     * 审批通过
     */
    public int approve(Long applyId, Long userId, String comment);

    /**
     * 审批驳回
     */
    public int reject(Long applyId, Long userId, String comment);
}