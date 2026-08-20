package com.ruoyi.oa.todo.mapper;

import java.util.List;
import com.ruoyi.oa.todo.domain.OaTodoItem;

/**
 * 待办事项Mapper接口
 * 
 * @author oa
 */
public interface OaTodoItemMapper
{
    /**
     * 查询待办事项
     */
    public OaTodoItem selectOaTodoItemById(Long todoId);

    /**
     * 查询待办事项列表
     */
    public List<OaTodoItem> selectOaTodoItemList(OaTodoItem oaTodoItem);

    /**
     * 新增待办事项
     */
    public int insertOaTodoItem(OaTodoItem oaTodoItem);

    /**
     * 修改待办事项
     */
    public int updateOaTodoItem(OaTodoItem oaTodoItem);

    /**
     * 删除待办事项
     */
    public int deleteOaTodoItemById(Long todoId);

    /**
     * 批量删除待办事项
     */
    public int deleteOaTodoItemByIds(Long[] todoIds);
}