package com.ruoyi.oa.contacts.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.oa.contacts.domain.OaContactPerson;
import com.ruoyi.oa.contacts.service.IOaContactsService;

/**
 * 通讯录 接口
 * 
 * @author oa
 */
@RestController
@RequestMapping("/contacts")
public class OaContactsController extends BaseController
{
    @Autowired
    private IOaContactsService contactsService;

    /**
     * 通讯录人员列表（可按 deptId/name 查询）
     */
    @GetMapping("/list")
    public TableDataInfo list(OaContactPerson contact)
    {
        startPage();
        List<OaContactPerson> list = contactsService.selectContactList(contact);
        return getDataTable(list);
    }

    /**
     * 通讯录人员详情
     */
    @GetMapping("/{personId}")
    public AjaxResult getInfo(@PathVariable Long personId)
    {
        return success(contactsService.selectContactById(personId));
    }

    /**
     * 新增通讯录人员
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody OaContactPerson contact)
    {
        return toAjax(contactsService.insertContact(contact));
    }

    /**
     * 修改通讯录人员
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody OaContactPerson contact)
    {
        return toAjax(contactsService.updateContact(contact));
    }

    /**
     * 删除通讯录人员
     */
    @DeleteMapping("/{personIds}")
    public AjaxResult remove(@PathVariable Long[] personIds)
    {
        return toAjax(contactsService.deleteContactByIds(personIds));
    }
}