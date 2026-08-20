package com.ruoyi.oa.api;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.factory.RemoteOaApprovalFallbackFactory;
import com.ruoyi.oa.approval.domain.OaApprovalApply;

/**
 * OA 审批服务
 * 
 * @author oa
 */
@FeignClient(contextId = "remoteOaApprovalService", value = ServiceNameConstants.OA_APPROVAL_SERVICE, fallbackFactory = RemoteOaApprovalFallbackFactory.class)
public interface RemoteOaApprovalService
{
    /**
     * 申请列表（供工作台聚合服务）
     */
    @GetMapping("/approval/inner/list")
    public R<List<OaApprovalApply>> listApproval();
}
