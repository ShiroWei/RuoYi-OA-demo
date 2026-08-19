package com.ruoyi.oa.dashboard.service;

import java.util.Map;

/**
 * 工作台统计Service接口
 * 
 * @author oa
 */
public interface IOaDashboardService
{
    /**
     * 统计面板数据
     */
    Map<String, Object> panel();

    /**
     * 近7日趋势（expectedData 每日申请数 / actualData 每日通过数）
     */
    Map<String, Object> line();

    /**
     * 近9周各部门申请量（pageA 请假 / pageB 报销 / pageC 出差）
     */
    Map<String, Object> bar();

    /**
     * 审批类型分布
     */
    Map<String, Object> pie();
}