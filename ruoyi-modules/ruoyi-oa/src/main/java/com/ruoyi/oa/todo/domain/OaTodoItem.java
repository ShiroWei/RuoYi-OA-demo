package com.ruoyi.oa.todo.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 待办事项对象 todo_item
 * 
 * @author oa
 */
public class OaTodoItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 待办ID */
    private Long todoId;

    /** 关联业务类型（approval 审批） */
    private String bizType;

    /** 关联业务ID */
    private Long bizId;

    /** 待办标题 */
    private String title;

    /** 待办类型（请假/报销/出差/用章） */
    private String todoType;

    /** 提交人 */
    private String submitter;

    /** 处理人ID */
    private Long handlerId;

    /** 优先级（高/中/低） */
    private String priority;

    /** 状态（0待处理 1已处理） */
    private String status;

    /** 到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueTime;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    public Long getTodoId()
    {
        return todoId;
    }

    public void setTodoId(Long todoId)
    {
        this.todoId = todoId;
    }

    public String getBizType()
    {
        return bizType;
    }

    public void setBizType(String bizType)
    {
        this.bizType = bizType;
    }

    public Long getBizId()
    {
        return bizId;
    }

    public void setBizId(Long bizId)
    {
        this.bizId = bizId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTodoType()
    {
        return todoType;
    }

    public void setTodoType(String todoType)
    {
        this.todoType = todoType;
    }

    public String getSubmitter()
    {
        return submitter;
    }

    public void setSubmitter(String submitter)
    {
        this.submitter = submitter;
    }

    public Long getHandlerId()
    {
        return handlerId;
    }

    public void setHandlerId(Long handlerId)
    {
        this.handlerId = handlerId;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getDueTime()
    {
        return dueTime;
    }

    public void setDueTime(Date dueTime)
    {
        this.dueTime = dueTime;
    }

    public Date getSubmitTime()
    {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime)
    {
        this.submitTime = submitTime;
    }
}