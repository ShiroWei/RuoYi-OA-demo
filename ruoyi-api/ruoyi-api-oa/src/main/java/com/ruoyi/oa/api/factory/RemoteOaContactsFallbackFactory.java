package com.ruoyi.oa.api.factory;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.RemoteOaContactsService;
import com.ruoyi.oa.contacts.domain.OaContactPerson;

/**
 * OA 通讯录服务降级处理
 * 
 * @author oa
 */
@Component
public class RemoteOaContactsFallbackFactory implements FallbackFactory<RemoteOaContactsService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteOaContactsFallbackFactory.class);

    @Override
    public RemoteOaContactsService create(Throwable throwable)
    {
        log.error("OA 通讯录服务调用失败:{}", throwable.getMessage());
        return new RemoteOaContactsService()
        {
            @Override
            public R<List<OaContactPerson>> listContact()
            {
                return R.fail("获取通讯录失败:" + throwable.getMessage());
            }
        };
    }
}
