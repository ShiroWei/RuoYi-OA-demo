package com.ruoyi.oa.calendar.mapper;

import java.util.List;
import com.ruoyi.oa.calendar.domain.OaScheduleEvent;

/**
 * 日程事件Mapper接口
 * 
 * @author oa
 */
public interface OaScheduleEventMapper
{
    /**
     * 查询日程事件
     */
    public OaScheduleEvent selectOaScheduleEventById(Long eventId);

    /**
     * 查询日程事件列表（可按月份前缀查询）
     */
    public List<OaScheduleEvent> selectOaScheduleEventList(OaScheduleEvent oaScheduleEvent);

    /**
     * 新增日程事件
     */
    public int insertOaScheduleEvent(OaScheduleEvent oaScheduleEvent);

    /**
     * 修改日程事件
     */
    public int updateOaScheduleEvent(OaScheduleEvent oaScheduleEvent);

    /**
     * 删除日程事件
     */
    public int deleteOaScheduleEventById(Long eventId);

    /**
     * 批量删除日程事件
     */
    public int deleteOaScheduleEventByIds(Long[] eventIds);
}