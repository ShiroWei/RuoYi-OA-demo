package com.ruoyi.oa.api.factory;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.oa.api.RemoteOaTodoService;
import com.ruoyi.oa.todo.domain.OaTodoItem;

/**
 * OA 待办服务降级处理
 * 
 * @author oa
 */
@Component
public class RemoteOaTodoFallbackFactory implements FallbackFactory<RemoteOaTodoService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteOaTodoFallbackFactory.class);

    @Override
    public RemoteOaTodoService create(Throwable throwable)
    {
        log.error("OA 待办服务调用失败:{}", throwable.getMessage());
        return new RemoteOaTodoService()
        {
            @Override
            public R<Boolean> createTodo(OaTodoItem todo)
            {
                return R.fail("创建待办失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> completeTodoByApplyId(Long applyId)
            {
                return R.fail("完成待办失败:" + throwable.getMessage());
            }

            @Override
            public R<List<OaTodoItem>> listTodo()
            {
                return R.fail("获取待办列表失败:" + throwable.getMessage());
            }
        };
    }
}
