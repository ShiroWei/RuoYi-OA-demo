package com.ruoyi.oa.dashboard.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.RemoteOaApprovalService;
import com.ruoyi.oa.api.RemoteOaContactsService;
import com.ruoyi.oa.api.RemoteOaTodoService;
import com.ruoyi.oa.approval.domain.OaApprovalApply;
import com.ruoyi.oa.contacts.domain.OaContactPerson;
import com.ruoyi.oa.dashboard.service.IOaDashboardService;
import com.ruoyi.oa.todo.domain.OaTodoItem;

/**
 * 工作台统计Service业务层处理（Feign 聚合各业务服务）
 * 
 * @author oa
 */
@Service
public class OaDashboardServiceImpl implements IOaDashboardService
{
    private static final String[] WEEK_TYPES = { "请假", "报销", "出差" };

    @Autowired
    private RemoteOaApprovalService remoteApprovalService;

    @Autowired
    private RemoteOaTodoService remoteTodoService;

    @Autowired
    private RemoteOaContactsService remoteContactsService;

    /**
     * 统计面板数据（msgCount 未读数由前端公告接口填充）
     */
    @Override
    public Map<String, Object> panel()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        List<OaTodoItem> todos = todoList();
        map.put("todoCount", todos.stream().filter(t -> "0".equals(t.getStatus())).count());

        List<OaApprovalApply> applies = approvalList();
        map.put("leaveCount", applies.stream().filter(a -> "请假".equals(a.getApplyType()) && "0".equals(a.getStatus())).count());

        LocalDate now = LocalDate.now();
        long finishCount = applies.stream()
                .filter(a -> "1".equals(a.getStatus()))
                .map(a -> toLocalDate(a.getApplyTime()))
                .filter(d -> d != null)
                .filter(d -> d.getYear() == now.getYear() && d.getMonthValue() == now.getMonthValue())
                .count();
        map.put("finishCount", finishCount);

        map.put("todayOnline", contactList().size());

        long weekLeave = applies.stream()
                .filter(a -> "请假".equals(a.getApplyType()))
                .map(a -> toLocalDate(a.getApplyTime()))
                .filter(d -> d != null && isThisWeek(d))
                .count();
        map.put("weekLeave", weekLeave);
        return map;
    }

    /**
     * 近7日趋势：expectedData 每日申请数 / actualData 每日通过数
     */
    @Override
    public Map<String, Object> line()
    {
        List<OaApprovalApply> applies = approvalList();
        int[] expected = new int[7];
        int[] actual = new int[7];
        LocalDate today = LocalDate.now();
        for (OaApprovalApply a : applies)
        {
            LocalDate d = toLocalDate(a.getApplyTime());
            if (d == null)
            {
                continue;
            }
            int idx = (int) ChronoUnit.DAYS.between(d, today);
            if (idx >= 0 && idx < 7)
            {
                expected[6 - idx]++;
                if ("1".equals(a.getStatus()))
                {
                    actual[6 - idx]++;
                }
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("expectedData", expected);
        map.put("actualData", actual);
        return map;
    }

    /**
     * 近9周申请量：pageA 请假 / pageB 报销 / pageC 出差（每周计数）
     */
    @Override
    public Map<String, Object> bar()
    {
        List<OaApprovalApply> applies = approvalList();
        int[][] weeks = new int[3][9];
        LocalDate today = LocalDate.now();
        for (OaApprovalApply a : applies)
        {
            LocalDate d = toLocalDate(a.getApplyTime());
            if (d == null)
            {
                continue;
            }
            int typeIdx = indexOf(WEEK_TYPES, a.getApplyType());
            if (typeIdx < 0)
            {
                continue;
            }
            int diff = (int) ChronoUnit.WEEKS.between(weekStart(d), weekStart(today));
            if (diff >= 0 && diff < 9)
            {
                weeks[typeIdx][8 - diff]++;
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pageA", weeks[0]);
        map.put("pageB", weeks[1]);
        map.put("pageC", weeks[2]);
        return map;
    }

    /**
     * 审批类型分布
     */
    @Override
    public Map<String, Object> pie()
    {
        List<OaApprovalApply> applies = approvalList();
        Map<String, Integer> count = new LinkedHashMap<String, Integer>();
        for (OaApprovalApply a : applies)
        {
            String type = a.getApplyType() == null || a.getApplyType().isEmpty() ? "其他" : a.getApplyType();
            count.put(type, count.getOrDefault(type, 0) + 1);
        }
        List<Map<String, Object>> series = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, Integer> e : count.entrySet())
        {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("value", e.getValue());
            item.put("name", e.getKey());
            series.add(item);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("series", series);
        return map;
    }

    private List<OaApprovalApply> approvalList()
    {
        R<List<OaApprovalApply>> r = remoteApprovalService.listApproval();
        return R.isSuccess(r) && r.getData() != null ? r.getData() : new ArrayList<OaApprovalApply>();
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

    private LocalDate toLocalDate(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDate weekStart(LocalDate d)
    {
        return d.minusDays(d.getDayOfWeek().getValue() - 1);
    }

    private int indexOf(String[] arr, String v)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i].equals(v))
            {
                return i;
            }
        }
        return -1;
    }

    private boolean isThisWeek(LocalDate d)
    {
        if (d == null)
        {
            return false;
        }
        return weekStart(d).equals(weekStart(LocalDate.now()));
    }
}
