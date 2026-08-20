package com.ruoyi.oa.todo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.oa.todo.domain.OaTodoItem;
import com.ruoyi.oa.todo.mapper.OaTodoItemMapper;
import com.ruoyi.oa.todo.service.IOaTodoService;

/**
 * 待办中心Service业务层处理
 * 
 * @author oa
 */
@Service
public class OaTodoServiceImpl implements IOaTodoService
{
    @Autowired
    private OaTodoItemMapper todoItemMapper;

    /**
     * 待办中心顶部统计
     */
    @Override
    public Map<String, Object> selectTodoStat(Long userId)
    {
        Map<String, Object> stat = new HashMap<String, Object>();
        OaTodoItem pending = new OaTodoItem();
        pending.setStatus("0");
        List<OaTodoItem> pendingList = todoItemMapper.selectOaTodoItemList(pending);
        stat.put("todoCount", pendingList.size());

        OaTodoItem done = new OaTodoItem();
        done.setStatus("1");
        stat.put("doneCount", todoItemMapper.selectOaTodoItemList(done).size());

        OaTodoItem apply = new OaTodoItem();
        apply.setSubmitter(SecurityUtils.getUsername());
        stat.put("applyCount", todoItemMapper.selectOaTodoItemList(apply).size());
        stat.put("overdueCount", 0);
        return stat;
    }

    /**
     * 查询待办列表
     */
    @Override
    public List<OaTodoItem> selectTodoList(String type, Long userId)
    {
        OaTodoItem query = new OaTodoItem();
        if ("done".equals(type))
        {
            query.setStatus("1");
        }
        else if ("apply".equals(type))
        {
            query.setSubmitter(SecurityUtils.getUsername());
        }
        else
        {
            query.setStatus("0");
        }
        return todoItemMapper.selectOaTodoItemList(query);
    }

    /**
     * 标记待办已处理
     */
    @Override
    public int completeTodo(Long todoId)
    {
        OaTodoItem todo = todoItemMapper.selectOaTodoItemById(todoId);
        if (todo == null)
        {
            return 0;
        }
        todo.setStatus("1");
        return todoItemMapper.updateOaTodoItem(todo);
    }

    /**
     * 内部：新增待办
     */
    @Override
    public int createTodo(OaTodoItem todo)
    {
        return todoItemMapper.insertOaTodoItem(todo);
    }

    /**
     * 内部：按申请ID完成待办
     */
    @Override
    public int completeTodoByApplyId(Long applyId)
    {
        OaTodoItem query = new OaTodoItem();
        query.setBizId(applyId);
        query.setStatus("0");
        List<OaTodoItem> todos = todoItemMapper.selectOaTodoItemList(query);
        for (OaTodoItem todo : todos)
        {
            todo.setStatus("1");
            todoItemMapper.updateOaTodoItem(todo);
        }
        return todos.size();
    }

    /**
     * 内部：查询全部待办
     */
    @Override
    public List<OaTodoItem> selectAllList()
    {
        return todoItemMapper.selectOaTodoItemList(new OaTodoItem());
    }
}
