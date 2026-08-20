package com.ruoyi.oa.ai.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.ai.service.IOaAiService;
import com.ruoyi.oa.api.RemoteOaApprovalService;
import com.ruoyi.oa.api.RemoteOaCalendarService;
import com.ruoyi.oa.api.RemoteOaContactsService;
import com.ruoyi.oa.api.RemoteOaTodoService;
import com.ruoyi.oa.approval.domain.OaApprovalApply;
import com.ruoyi.oa.calendar.domain.OaScheduleEvent;
import com.ruoyi.oa.contacts.domain.OaContactPerson;
import com.ruoyi.oa.todo.domain.OaTodoItem;

/**
 * 智能助手Service业务层处理（Feign 聚合各业务服务）
 * <p>当前实现为关键词规则 + 真实业务统计的 mock，返回结构化 JSON 供前端渲染；
 * 后续接入真实大模型时，仅需替换本实现（保持 chat 返回结构不变）。</p>
 * 
 * @author oa
 */
@Service
public class OaAiServiceImpl implements IOaAiService
{
    @Autowired
    private RemoteOaApprovalService remoteApprovalService;

    @Autowired
    private RemoteOaTodoService remoteTodoService;

    @Autowired
    private RemoteOaContactsService remoteContactsService;

    @Autowired
    private RemoteOaCalendarService remoteCalendarService;

    @Override
    public Map<String, Object> chat(String message)
    {
        String msg = message == null ? "" : message.trim();
        if (msg.isEmpty())
        {
            return helpReply();
        }
        if (containsAny(msg, "待办", "待处理", "我的任务", "需要我处理"))
        {
            return todoReply();
        }
        if (containsAny(msg, "请假"))
        {
            return applyTypeReply("请假");
        }
        if (containsAny(msg, "报销"))
        {
            return applyTypeReply("报销");
        }
        if (containsAny(msg, "出差"))
        {
            return applyTypeReply("出差");
        }
        if (containsAny(msg, "日程", "会议", "安排"))
        {
            return scheduleReply();
        }
        if (containsAny(msg, "通讯录", "同事", "人员"))
        {
            return contactsReply();
        }
        if (containsAny(msg, "统计", "概览", "总结", "报表", "工作台"))
        {
            return statReply();
        }
        if (containsAny(msg, "审批", "申请", "进度"))
        {
            return applySummaryReply();
        }
        if (containsAny(msg, "帮助", "能做什么", "功能", "怎么用", "你好", "您好", "hi", "hello"))
        {
            return helpReply();
        }
        return helpReply();
    }

    private Map<String, Object> todoReply()
    {
        List<OaTodoItem> todos = new ArrayList<OaTodoItem>();
        for (OaTodoItem t : todoList())
        {
            if ("0".equals(t.getStatus()))
            {
                todos.add(t);
            }
        }
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (OaTodoItem t : todos)
        {
            Map<String, Object> it = new HashMap<String, Object>();
            it.put("title", t.getTitle());
            it.put("desc", joinDesc(t.getTodoType(), t.getSubmitter(), fmt(t.getSubmitTime(), "MM-dd HH:mm")));
            it.put("jumpTo", "/oa/approval/detail/" + t.getBizId());
            items.add(it);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reply", "您当前有 " + todos.size() + " 条待办事项：");
        map.put("type", "list");
        map.put("items", items);
        map.put("jumpUrl", "/oa/todo");
        map.put("action", "jump");
        return map;
    }

    private Map<String, Object> applyTypeReply(String type)
    {
        List<OaApprovalApply> applies = listApply(type);
        int pending = 0;
        int passed = 0;
        int rejected = 0;
        for (OaApprovalApply a : applies)
        {
            if ("0".equals(a.getStatus()))
            {
                pending++;
            }
            else if ("1".equals(a.getStatus()))
            {
                passed++;
            }
            else if ("2".equals(a.getStatus()))
            {
                rejected++;
            }
        }
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (OaApprovalApply a : first3(applies))
        {
            Map<String, Object> it = new HashMap<String, Object>();
            it.put("title", a.getTitle());
            it.put("desc", joinDesc(statusText(a.getStatus()), a.getApplicant(), fmt(a.getApplyTime(), "MM-dd HH:mm")));
            it.put("jumpTo", "/oa/approval/detail/" + a.getApplyId());
            items.add(it);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reply", type + "申请共 " + applies.size() + " 条（待审批 " + pending + " / 已通过 " + passed + " / 已驳回 " + rejected + "）。最近" + type + "申请：");
        map.put("type", "list");
        map.put("items", items);
        map.put("jumpUrl", "/oa/approval");
        map.put("action", "jump");
        return map;
    }

    private Map<String, Object> scheduleReply()
    {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (OaScheduleEvent e : eventList())
        {
            if (e.getEventDate() != null && today.equals(fmt(e.getEventDate(), "yyyy-MM-dd")))
            {
                Map<String, Object> it = new HashMap<String, Object>();
                it.put("title", e.getTitle());
                it.put("desc", joinDesc(e.getStartTime(), e.getEndTime(), e.getLocation()));
                it.put("jumpTo", "/oa/calendar");
                items.add(it);
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reply", items.isEmpty() ? "今天暂无日程安排，可以好好规划一下。" : "今日共 " + items.size() + " 项日程：");
        map.put("type", "list");
        map.put("items", items);
        map.put("jumpUrl", "/oa/calendar");
        map.put("action", "jump");
        return map;
    }

    private Map<String, Object> contactsReply()
    {
        List<OaContactPerson> list = contactList();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reply", "公司通讯录共 " + list.size() + " 位同事。");
        map.put("type", "stat");
        map.put("items", stats(stat("同事总数", list.size() + "")));
        map.put("jumpUrl", "/oa/contacts");
        map.put("action", "jump");
        return map;
    }

    private Map<String, Object> statReply()
    {
        List<OaTodoItem> todos = todoList();
        int todo = 0;
        for (OaTodoItem t : todos)
        {
            if ("0".equals(t.getStatus()))
            {
                todo++;
            }
        }
        List<OaApprovalApply> applies = approvalList();
        int leave = 0;
        for (OaApprovalApply a : applies)
        {
            if ("请假".equals(a.getApplyType()) && "0".equals(a.getStatus()))
            {
                leave++;
            }
        }
        int finish = countFinishThisMonth(applies);
        int online = contactList().size();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reply", "工作台概览：待办 " + todo + " 条，请假审批中 " + leave + " 条，本月完成 " + finish + " 项，在职同事 " + online + " 人。");
        map.put("type", "stat");
        map.put("items", stats(stat("待办", todo + ""), stat("审批中", leave + ""), stat("本月完成", finish + ""), stat("同事", online + "")));
        return map;
    }

    private Map<String, Object> applySummaryReply()
    {
        List<OaApprovalApply> applies = approvalList();
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (String type : new String[] { "请假", "报销", "出差" })
        {
            int pending = 0;
            int passed = 0;
            int rejected = 0;
            for (OaApprovalApply a : applies)
            {
                if (!type.equals(a.getApplyType()))
                {
                    continue;
                }
                if ("0".equals(a.getStatus()))
                {
                    pending++;
                }
                else if ("1".equals(a.getStatus()))
                {
                    passed++;
                }
                else if ("2".equals(a.getStatus()))
                {
                    rejected++;
                }
            }
            items.add(stat(type, pending + " 审批中 / " + passed + " 已通过 / " + rejected + " 驳回"));
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reply", "各类审批申请进度如下（审批中 / 已通过 / 已驳回）：");
        map.put("type", "stat");
        map.put("items", items);
        map.put("jumpUrl", "/oa/approval");
        map.put("action", "jump");
        return map;
    }

    private Map<String, Object> helpReply()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reply", "您好，我是智能助手，可以帮您快速了解工作台情况。试试问我：\n" + "· 「我的待办」查看待处理事项\n" + "· 「请假 / 报销 / 出差」查看申请进度\n"
                + "· 「今日日程」查看当天安排\n" + "· 「同事 / 通讯录」查看在职人数\n" + "· 「统计 / 概览」查看工作台汇总");
        map.put("type", "text");
        map.put("items", new ArrayList<Map<String, Object>>());
        return map;
    }

    private List<OaApprovalApply> approvalList()
    {
        R<List<OaApprovalApply>> r = remoteApprovalService.listApproval();
        return R.isSuccess(r) && r.getData() != null ? r.getData() : new ArrayList<OaApprovalApply>();
    }

    private List<OaApprovalApply> listApply(String type)
    {
        List<OaApprovalApply> result = new ArrayList<OaApprovalApply>();
        for (OaApprovalApply a : approvalList())
        {
            if (type.equals(a.getApplyType()))
            {
                result.add(a);
            }
        }
        return result;
    }

    private List<OaTodoItem> todoList()
    {
        R<List<OaTodoItem>> r = remoteTodoService.listTodo();
        return R.isSuccess(r) && r.getData() != null ? r.getData() : new ArrayList<OaTodoItem>();
    }

    private List<OaContactPerson> contactList()
    {
        R<List<OaContactPerson>> r = remoteContactsService.listContact();
        return R.isSuccess(r) && r.getData() != null ? r.getData() : new ArrayList<OaContactPerson>();
    }

    private List<OaScheduleEvent> eventList()
    {
        R<List<OaScheduleEvent>> r = remoteCalendarService.listEvent();
        return R.isSuccess(r) && r.getData() != null ? r.getData() : new ArrayList<OaScheduleEvent>();
    }

    private int countFinishThisMonth(List<OaApprovalApply> applies)
    {
        String ym = new SimpleDateFormat("yyyy-MM").format(new Date());
        int count = 0;
        for (OaApprovalApply a : applies)
        {
            if ("1".equals(a.getStatus()) && a.getApplyTime() != null && ym.equals(new SimpleDateFormat("yyyy-MM").format(a.getApplyTime())))
            {
                count++;
            }
        }
        return count;
    }

    private List<OaApprovalApply> first3(List<OaApprovalApply> list)
    {
        return list.size() > 3 ? list.subList(0, 3) : list;
    }

    private String statusText(String status)
    {
        if ("0".equals(status))
        {
            return "待审批";
        }
        if ("1".equals(status))
        {
            return "已通过";
        }
        if ("2".equals(status))
        {
            return "已驳回";
        }
        return "";
    }

    private String fmt(Date date, String pattern)
    {
        return date == null ? "" : new SimpleDateFormat(pattern).format(date);
    }

    private String joinDesc(String... parts)
    {
        StringBuilder sb = new StringBuilder();
        for (String p : parts)
        {
            if (p != null && !p.isEmpty())
            {
                if (sb.length() > 0)
                {
                    sb.append(" · ");
                }
                sb.append(p);
            }
        }
        return sb.toString();
    }

    private List<Map<String, Object>> stats(Map<String, Object>... items)
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> it : items)
        {
            list.add(it);
        }
        return list;
    }

    private Map<String, Object> stat(String name, String value)
    {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("name", name);
        m.put("value", value);
        return m;
    }

    private boolean containsAny(String msg, String... keywords)
    {
        String lower = msg.toLowerCase();
        for (String k : keywords)
        {
            if (lower.contains(k.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }
}
