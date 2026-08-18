package com.ruoyi.oa.todo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        apply.setSubmitter(null);
        stat.put("applyCount", 0);
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
        else
        {
            query.setStatus("0");
        }
        if ("apply".equals(type))
        {
            query.setHandlerId(userId);
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
}