package com.ruoyi.oa.api;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.factory.RemoteOaContactsFallbackFactory;
import com.ruoyi.oa.contacts.domain.OaContactPerson;

/**
 * OA 通讯录服务
 * 
 * @author oa
 */
@FeignClient(contextId = "remoteOaContactsService", value = ServiceNameConstants.OA_CONTACTS_SERVICE, fallbackFactory = RemoteOaContactsFallbackFactory.class)
public interface RemoteOaContactsService
{
    /**
     * 通讯录人员列表（供工作台聚合服务）
     */
    @GetMapping("/contacts/inner/list")
    public R<List<OaContactPerson>> listContact();
}
