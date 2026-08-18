package com.ruoyi.oa.todo.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.oa.todo.domain.OaTodoItem;
import com.ruoyi.oa.todo.service.IOaTodoService;

/**
 * 待办中心 接口
 * 
 * @author oa
 */
@RestController
@RequestMapping("/todo")
public class OaTodoController extends BaseController
{
    @Autowired
    private IOaTodoService todoService;

    /**
     * 待办中心顶部统计
     */
    @GetMapping("/stat")
    public AjaxResult stat()
    {
        return success(todoService.selectTodoStat(SecurityUtils.getUserId()));
    }

    /**
     * 待办列表（type: pending 待我审批 / done 我已处理 / apply 我发起的）
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "type", required = false) String type)
    {
        startPage();
        List<OaTodoItem> list = todoService.selectTodoList(type, SecurityUtils.getUserId());
        return getDataTable(list);
    }

    /**
     * 标记待办已处理
     */
    @PostMapping("/complete/{todoId}")
    public AjaxResult complete(@PathVariable Long todoId)
    {
        return toAjax(todoService.completeTodo(todoId));
    }
}