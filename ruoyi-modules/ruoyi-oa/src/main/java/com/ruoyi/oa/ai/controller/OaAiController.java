package com.ruoyi.oa.ai.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.oa.ai.service.IOaAiService;

/**
 * 智能助手 接口
 * 
 * @author oa
 */
@RestController
@RequestMapping("/ai")
public class OaAiController extends BaseController
{
    @Autowired
    private IOaAiService aiService;

    /**
     * 智能问答（当前为规则 mock，后续可接入真实大模型）
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, String> body)
    {
        String message = body == null || body.get("message") == null ? "" : body.get("message");
        return success(aiService.chat(message));
    }
}
