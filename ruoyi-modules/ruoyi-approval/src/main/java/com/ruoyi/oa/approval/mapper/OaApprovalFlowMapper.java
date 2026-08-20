package com.ruoyi.oa.approval.mapper;

import java.util.List;
import com.ruoyi.oa.approval.domain.OaApprovalFlow;

/**
 * 审批流程节点Mapper接口
 * 
 * @author oa
 */
public interface OaApprovalFlowMapper
{
    /**
     * 查询审批流程节点
     */
    public OaApprovalFlow selectOaApprovalFlowById(Long flowId);

    /**
     * 查询审批流程节点列表
     */
    public List<OaApprovalFlow> selectOaApprovalFlowList(OaApprovalFlow oaApprovalFlow);

    /**
     * 查询某申请的流程节点（按顺序）
     */
    public List<OaApprovalFlow> selectFlowByApplyId(Long applyId);

    /**
     * 新增审批流程节点
     */
    public int insertOaApprovalFlow(OaApprovalFlow oaApprovalFlow);

    /**
     * 修改审批流程节点
     */
    public int updateOaApprovalFlow(OaApprovalFlow oaApprovalFlow);

    /**
     * 删除审批流程节点
     */
    public int deleteOaApprovalFlowById(Long flowId);

    /**
     * 删除某申请的流程节点
     */
    public int deleteFlowByApplyId(Long applyId);
}