package com.ruoyi.oa.calendar.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 日程事件对象 schedule_event
 * 
 * @author oa
 */
public class OaScheduleEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日程ID */
    private Long eventId;

    /** 标题 */
    private String title;

    /** 事件日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date eventDate;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;

    /** 地点 */
    private String location;

    /** 类型（会议/汇报/活动） */
    private String eventType;

    /** 创建人ID */
    private Long createById;

    /** 参与人 */
    private String participants;

    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getEventDate()
    {
        return eventDate;
    }

    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public Long getCreateById()
    {
        return createById;
    }

    public void setCreateById(Long createById)
    {
        this.createById = createById;
    }

    public String getParticipants()
    {
        return participants;
    }

    public void setParticipants(String participants)
    {
        this.participants = participants;
    }
}