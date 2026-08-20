package com.ruoyi.oa.todo.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.oa.todo.domain.OaTodoItem;

/**
 * 待办中心Service接口
 * 
 * @author oa
 */
public interface IOaTodoService
{
    /**
     * 待办中心顶部统计
     */
    public Map<String, Object> selectTodoStat(Long userId);

    /**
     * 查询待办列表（type: pending 待我审批 / done 我已处理 / apply 我发起的）
     */
    public List<OaTodoItem> selectTodoList(String type, Long userId);

    /**
     * 标记待办已处理
     */
    public int completeTodo(Long todoId);

    /**
     * 内部：新增待办（供审批服务联动）
     */
    public int createTodo(OaTodoItem todo);

    /**
     * 内部：按申请ID完成待办（供审批服务联动）
     */
    public int completeTodoByApplyId(Long applyId);

    /**
     * 内部：查询全部待办（供工作台聚合服务）
     */
    public List<OaTodoItem> selectAllList();
}
