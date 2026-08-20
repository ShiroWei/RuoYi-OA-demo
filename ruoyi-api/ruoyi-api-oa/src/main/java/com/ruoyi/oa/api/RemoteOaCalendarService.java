package com.ruoyi.oa.api;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.factory.RemoteOaCalendarFallbackFactory;
import com.ruoyi.oa.calendar.domain.OaScheduleEvent;

/**
 * OA 日程服务
 * 
 * @author oa
 */
@FeignClient(contextId = "remoteOaCalendarService", value = ServiceNameConstants.OA_CALENDAR_SERVICE, fallbackFactory = RemoteOaCalendarFallbackFactory.class)
public interface RemoteOaCalendarService
{
    /**
     * 日程列表（供工作台聚合服务）
     */
    @GetMapping("/calendar/inner/list")
    public R<List<OaScheduleEvent>> listEvent();
}
