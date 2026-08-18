package com.ruoyi.oa.calendar.service;

import java.util.List;
import com.ruoyi.oa.calendar.domain.OaScheduleEvent;

/**
 * 会议日程Service接口
 * 
 * @author oa
 */
public interface IOaCalendarService
{
    /**
     * 查询日程事件列表（month: yyyy-MM，可为空查全部）
     */
    public List<OaScheduleEvent> selectEventList(String month);

    /**
     * 查询日程事件详细
     */
    public OaScheduleEvent selectEventById(Long eventId);

    /**
     * 新增日程事件
     */
    public int insertEvent(OaScheduleEvent event);

    /**
     * 修改日程事件
     */
    public int updateEvent(OaScheduleEvent event);

    /**
     * 删除日程事件
     */
    public int deleteEventByIds(Long[] eventIds);
}