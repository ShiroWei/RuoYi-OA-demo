package com.ruoyi.oa.calendar.domain;

import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 日程事件对象 oa_schedule_event
 * 
 * @author oa
 */
public class OaScheduleEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日程ID */
    private Long eventId;

    /** 标题 */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过200个字符")
    private String title;

    /** 事件日期 */
    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
    private Date eventDate;

    /** 开始时间 */
    @NotBlank(message = "开始时间不能为空")
    @Pattern(regexp = "([01][0-9]|2[0-3]):[0-5][0-9]", message = "开始时间格式必须为HH:mm")
    private String startTime;

    /** 结束时间 */
    @NotBlank(message = "结束时间不能为空")
    @Pattern(regexp = "([01][0-9]|2[0-3]):[0-5][0-9]", message = "结束时间格式必须为HH:mm")
    private String endTime;

    /** 地点 */
    @Size(max = 200, message = "地点不能超过200个字符")
    private String location;

    /** 类型（会议/汇报/活动） */
    @NotBlank(message = "类型不能为空")
    @Size(max = 32, message = "类型不能超过32个字符")
    private String eventType;

    /** 创建人ID */
    private Long createById;

    /** 参与人 */
    @Size(max = 500, message = "参与人不能超过500个字符")
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
