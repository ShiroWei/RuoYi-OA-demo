package com.ruoyi.oa.calendar.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.oa.calendar.domain.OaScheduleEvent;
import com.ruoyi.oa.calendar.mapper.OaScheduleEventMapper;
import com.ruoyi.oa.calendar.service.IOaCalendarService;

/**
 * 会议日程Service业务层处理
 * 
 * @author oa
 */
@Service
public class OaCalendarServiceImpl implements IOaCalendarService
{
    @Autowired
    private OaScheduleEventMapper scheduleEventMapper;

    /**
     * 查询日程事件列表
     */
    @Override
    public List<OaScheduleEvent> selectEventList(String month)
    {
        OaScheduleEvent query = new OaScheduleEvent();
        if (month != null && !month.isEmpty())
        {
            query.setEventDate(com.ruoyi.common.core.utils.DateUtils.parseDate(month + "-01"));
        }
        return scheduleEventMapper.selectOaScheduleEventList(query);
    }

    /**
     * 查询日程事件详细
     */
    @Override
    public OaScheduleEvent selectEventById(Long eventId)
    {
        return scheduleEventMapper.selectOaScheduleEventById(eventId);
    }

    /**
     * 新增日程事件
     */
    @Override
    public int insertEvent(OaScheduleEvent event)
    {
        return scheduleEventMapper.insertOaScheduleEvent(event);
    }

    /**
     * 修改日程事件
     */
    @Override
    public int updateEvent(OaScheduleEvent event)
    {
        return scheduleEventMapper.updateOaScheduleEvent(event);
    }

    /**
     * 删除日程事件
     */
    @Override
    public int deleteEventByIds(Long[] eventIds)
    {
        return scheduleEventMapper.deleteOaScheduleEventByIds(eventIds);
    }
}