package com.ruoyi.oa.approval.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.oa.approval.domain.OaApprovalApply;
import com.ruoyi.oa.approval.domain.OaApprovalFlow;
import com.ruoyi.oa.approval.service.IOaApprovalService;

/**
 * 审批中心 接口
 * 
 * @author oa
 */
@RestController
@RequestMapping("/approval")
public class OaApprovalController extends BaseController
{
    @Autowired
    private IOaApprovalService approvalService;

    /**
     * 审批列表（type: todo 待我审批 / done 我已审批 / apply 我发起的）
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "type", required = false) String type)
    {
        startPage();
        List<OaApprovalApply> list = approvalService.selectApprovalList(type, SecurityUtils.getUserId());
        return getDataTable(list);
    }

    /**
     * 审批详情
     */
    @GetMapping("/{applyId}")
    public AjaxResult getInfo(@PathVariable Long applyId)
    {
        return success(approvalService.selectApprovalById(applyId));
    }

    /**
     * 审批流程时间线
     */
    @GetMapping("/flow/{applyId}")
    public AjaxResult flow(@PathVariable Long applyId)
    {
        return success(approvalService.selectApprovalFlow(applyId));
    }

    /**
     * 发起申请
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody OaApprovalApply apply)
    {
        apply.setApplicantId(SecurityUtils.getUserId());
        apply.setApplicant(SecurityUtils.getUsername());
        approvalService.insertApproval(apply);
        AjaxResult result = success();
        result.put("id", apply.getApplyId());
        result.put("applyNo", apply.getApplyNo());
        return result;
    }

    /**
     * 审批通过
     */
    @PostMapping("/approve/{applyId}")
    public AjaxResult approve(@PathVariable Long applyId, String comment)
    {
        return toAjax(approvalService.approve(applyId, SecurityUtils.getUserId(), comment));
    }

    /**
     * 审批驳回
     */
    @PostMapping("/reject/{applyId}")
    public AjaxResult reject(@PathVariable Long applyId, String comment)
    {
        return toAjax(approvalService.reject(applyId, SecurityUtils.getUserId(), comment));
    }

    /**
     * 删除申请
     */
    @DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds)
    {
        return toAjax(1);
    }
}