package com.ruoyi.oa.ai.service;

import java.util.Map;

/**
 * 智能助手Service接口
 * 
 * @author oa
 */
public interface IOaAiService
{
    /**
     * 智能问答（当前为规则 mock，返回结构化 JSON；后续可替换为真实模型实现）
     */
    Map<String, Object> chat(String message);
}
