package com.ruoyi.oa.api.factory;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.RemoteOaCalendarService;
import com.ruoyi.oa.calendar.domain.OaScheduleEvent;

/**
 * OA 日程服务降级处理
 * 
 * @author oa
 */
@Component
public class RemoteOaCalendarFallbackFactory implements FallbackFactory<RemoteOaCalendarService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteOaCalendarFallbackFactory.class);

    @Override
    public RemoteOaCalendarService create(Throwable throwable)
    {
        log.error("OA 日程服务调用失败:{}", throwable.getMessage());
        return new RemoteOaCalendarService()
        {
            @Override
            public R<List<OaScheduleEvent>> listEvent()
            {
                return R.fail("获取日程失败:" + throwable.getMessage());
            }
        };
    }
}
