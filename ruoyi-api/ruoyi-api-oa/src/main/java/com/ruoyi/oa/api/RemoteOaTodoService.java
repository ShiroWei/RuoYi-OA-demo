package com.ruoyi.oa.api;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.factory.RemoteOaTodoFallbackFactory;
import com.ruoyi.oa.todo.domain.OaTodoItem;

/**
 * OA 待办服务
 * 
 * @author oa
 */
@FeignClient(contextId = "remoteOaTodoService", value = ServiceNameConstants.OA_TODO_SERVICE, fallbackFactory = RemoteOaTodoFallbackFactory.class)
public interface RemoteOaTodoService
{
    /**
     * 内部创建待办（供审批服务联动）
     */
    @PostMapping("/todo/inner/create")
    public R<Boolean> createTodo(@RequestBody OaTodoItem todo);

    /**
     * 按申请ID完成待办（供审批服务联动）
     */
    @PostMapping("/todo/inner/completeByApply/{applyId}")
    public R<Boolean> completeTodoByApplyId(@PathVariable("applyId") Long applyId);

    /**
     * 待办列表（供工作台聚合服务）
     */
    @GetMapping("/todo/inner/list")
    public R<List<OaTodoItem>> listTodo();
}
