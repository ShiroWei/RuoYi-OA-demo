package com.ruoyi.oa.approval.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 审批流程节点对象 approval_flow
 * 
 * @author oa
 */
public class OaApprovalFlow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流程ID */
    private Long flowId;

    /** 申请ID */
    private Long applyId;

    /** 环节名称 */
    private String nodeName;

    /** 环节顺序 */
    private Integer nodeOrder;

    /** 处理人 */
    private String handler;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /** 处理意见 */
    private String comment;

    /** 状态（finish 已处理 process 处理中 wait 待处理） */
    private String status;

    public Long getFlowId()
    {
        return flowId;
    }

    public void setFlowId(Long flowId)
    {
        this.flowId = flowId;
    }

    public Long getApplyId()
    {
        return applyId;
    }

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public Integer getNodeOrder()
    {
        return nodeOrder;
    }

    public void setNodeOrder(Integer nodeOrder)
    {
        this.nodeOrder = nodeOrder;
    }

    public String getHandler()
    {
        return handler;
    }

    public void setHandler(String handler)
    {
        this.handler = handler;
    }

    public Date getHandleTime()
    {
        return handleTime;
    }

    public void setHandleTime(Date handleTime)
    {
        this.handleTime = handleTime;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}