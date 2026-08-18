package com.ruoyi.oa.calendar.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.oa.calendar.domain.OaScheduleEvent;
import com.ruoyi.oa.calendar.service.IOaCalendarService;

/**
 * 会议日程 接口
 * 
 * @author oa
 */
@RestController
@RequestMapping("/calendar")
public class OaCalendarController extends BaseController
{
    @Autowired
    private IOaCalendarService calendarService;

    /**
     * 按月份获取日程事件（month: yyyy-MM）
     */
    @GetMapping("/events")
    public AjaxResult events(@RequestParam(value = "month", required = false) String month)
    {
        return success(calendarService.selectEventList(month));
    }

    /**
     * 日程详情
     */
    @GetMapping("/{eventId}")
    public AjaxResult getInfo(@PathVariable Long eventId)
    {
        return success(calendarService.selectEventById(eventId));
    }

    /**
     * 新增日程
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody OaScheduleEvent event)
    {
        event.setCreateById(SecurityUtils.getUserId());
        return toAjax(calendarService.insertEvent(event));
    }

    /**
     * 修改日程
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody OaScheduleEvent event)
    {
        return toAjax(calendarService.updateEvent(event));
    }

    /**
     * 删除日程
     */
    @DeleteMapping("/{eventIds}")
    public AjaxResult remove(@PathVariable Long[] eventIds)
    {
        return toAjax(calendarService.deleteEventByIds(eventIds));
    }
}