package com.ruoyi.oa.contacts.service;

import java.util.List;
import com.ruoyi.oa.contacts.domain.OaContactPerson;

/**
 * 通讯录Service接口
 * 
 * @author oa
 */
public interface IOaContactsService
{
    /**
     * 查询通讯录人员列表（可按部门/关键字查询）
     */
    public List<OaContactPerson> selectContactList(OaContactPerson contact);

    /**
     * 查询通讯录人员详细
     */
    public OaContactPerson selectContactById(Long personId);

    /**
     * 新增通讯录人员
     */
    public int insertContact(OaContactPerson contact);

    /**
     * 修改通讯录人员
     */
    public int updateContact(OaContactPerson contact);

    /**
     * 删除通讯录人员
     */
    public int deleteContactByIds(Long[] personIds);

    /**
     * 内部：查询全部人员（供工作台聚合服务）
     */
    public List<OaContactPerson> selectAllList();
}