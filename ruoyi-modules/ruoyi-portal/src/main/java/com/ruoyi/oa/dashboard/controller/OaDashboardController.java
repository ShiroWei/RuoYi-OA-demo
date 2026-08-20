package com.ruoyi.oa.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.oa.dashboard.service.IOaDashboardService;

/**
 * 工作台统计 接口
 * 
 * @author oa
 */
@RestController
@RequestMapping("/dashboard")
public class OaDashboardController extends BaseController
{
    @Autowired
    private IOaDashboardService dashboardService;

    /**
     * 统计面板数据
     */
    @GetMapping("/panel")
    public AjaxResult panel()
    {
        return success(dashboardService.panel());
    }

    /**
     * 近7日趋势
     */
    @GetMapping("/line")
    public AjaxResult line()
    {
        return success(dashboardService.line());
    }

    /**
     * 近9周各部门申请量
     */
    @GetMapping("/bar")
    public AjaxResult bar()
    {
        return success(dashboardService.bar());
    }

    /**
     * 审批类型分布
     */
    @GetMapping("/pie")
    public AjaxResult pie()
    {
        return success(dashboardService.pie());
    }
}