package com.ruoyi.oa.calendar.service.impl;

import java.util.List;
import java.time.YearMonth;
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
            if (!month.matches("[0-9]{4}-(0[1-9]|1[0-2])") || month.startsWith("0000"))
            {
                throw new com.ruoyi.common.core.exception.ServiceException("月份格式必须为yyyy-MM");
            }
            query.setEventDate(YearMonth.parse(month).atDay(1));
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

    /**
     * 内部：查询全部日程
     */
    @Override
    public List<OaScheduleEvent> selectAllList()
    {
        return scheduleEventMapper.selectOaScheduleEventList(new OaScheduleEvent());
    }
}
