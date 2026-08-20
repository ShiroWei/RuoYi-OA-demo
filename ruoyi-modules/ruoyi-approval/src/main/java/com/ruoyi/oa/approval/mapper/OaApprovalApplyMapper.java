package com.ruoyi.oa.approval.mapper;

import java.util.List;
import com.ruoyi.oa.approval.domain.OaApprovalApply;

/**
 * 审批申请Mapper接口
 * 
 * @author oa
 */
public interface OaApprovalApplyMapper
{
    /**
     * 查询审批申请
     */
    public OaApprovalApply selectOaApprovalApplyById(Long applyId);

    /**
     * 查询审批申请列表
     */
    public List<OaApprovalApply> selectOaApprovalApplyList(OaApprovalApply oaApprovalApply);

    /**
     * 新增审批申请
     */
    public int insertOaApprovalApply(OaApprovalApply oaApprovalApply);

    /**
     * 修改审批申请
     */
    public int updateOaApprovalApply(OaApprovalApply oaApprovalApply);

    /**
     * 删除审批申请
     */
    public int deleteOaApprovalApplyById(Long applyId);

    /**
     * 批量删除审批申请
     */
    public int deleteOaApprovalApplyByIds(Long[] applyIds);
}