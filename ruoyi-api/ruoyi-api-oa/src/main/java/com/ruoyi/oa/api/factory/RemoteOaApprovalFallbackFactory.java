package com.ruoyi.oa.api.factory;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.RemoteOaApprovalService;
import com.ruoyi.oa.approval.domain.OaApprovalApply;

/**
 * OA 审批服务降级处理
 * 
 * @author oa
 */
@Component
public class RemoteOaApprovalFallbackFactory implements FallbackFactory<RemoteOaApprovalService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteOaApprovalFallbackFactory.class);

    @Override
    public RemoteOaApprovalService create(Throwable throwable)
    {
        log.error("OA 审批服务调用失败:{}", throwable.getMessage());
        return new RemoteOaApprovalService()
        {
            @Override
            public R<List<OaApprovalApply>> listApproval()
            {
                return R.fail("获取审批申请失败:" + throwable.getMessage());
            }
        };
    }
}
