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
import com.ruoyi.oa.approval.domain.OaApprovalApply;
import com.ruoyi.oa.approval.mapper.OaApprovalApplyMapper;
import com.ruoyi.oa.contacts.domain.OaContactPerson;
import com.ruoyi.oa.contacts.mapper.OaContactPersonMapper;
import com.ruoyi.oa.dashboard.service.IOaDashboardService;
import com.ruoyi.oa.todo.domain.OaTodoItem;
import com.ruoyi.oa.todo.mapper.OaTodoItemMapper;

/**
 * 工作台统计Service业务层处理
 * 
 * @author oa
 */
@Service
public class OaDashboardServiceImpl implements IOaDashboardService
{
    private static final String[] WEEK_TYPES = { "请假", "报销", "出差" };

    @Autowired
    private OaApprovalApplyMapper approvalApplyMapper;

    @Autowired
    private OaTodoItemMapper todoItemMapper;

    @Autowired
    private OaContactPersonMapper contactPersonMapper;

    /**
     * 统计面板数据（msgCount 未读数由前端公告接口填充）
     */
    @Override
    public Map<String, Object> panel()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        OaTodoItem todoQuery = new OaTodoItem();
        todoQuery.setStatus("0");
        map.put("todoCount", todoItemMapper.selectOaTodoItemList(todoQuery).size());

        OaApprovalApply leaveQuery = new OaApprovalApply();
        leaveQuery.setApplyType("请假");
        leaveQuery.setStatus("0");
        map.put("leaveCount", approvalApplyMapper.selectOaApprovalApplyList(leaveQuery).size());

        OaApprovalApply finishQuery = new OaApprovalApply();
        finishQuery.setStatus("1");
        LocalDate now = LocalDate.now();
        long finishCount = approvalApplyMapper.selectOaApprovalApplyList(finishQuery).stream()
                .filter(a -> toLocalDate(a.getApplyTime()) != null)
                .filter(a -> toLocalDate(a.getApplyTime()).getYear() == now.getYear())
                .filter(a -> toLocalDate(a.getApplyTime()).getMonthValue() == now.getMonthValue())
                .count();
        map.put("finishCount", finishCount);

        map.put("todayOnline", contactPersonMapper.selectOaContactPersonList(new OaContactPerson()).size());

        OaApprovalApply weekLeaveQuery = new OaApprovalApply();
        weekLeaveQuery.setApplyType("请假");
        long weekLeave = approvalApplyMapper.selectOaApprovalApplyList(weekLeaveQuery).stream()
                .filter(a -> isThisWeek(toLocalDate(a.getApplyTime())))
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
        List<OaApprovalApply> applies = approvalApplyMapper.selectOaApprovalApplyList(new OaApprovalApply());
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
        List<OaApprovalApply> applies = approvalApplyMapper.selectOaApprovalApplyList(new OaApprovalApply());
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
        List<OaApprovalApply> applies = approvalApplyMapper.selectOaApprovalApplyList(new OaApprovalApply());
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