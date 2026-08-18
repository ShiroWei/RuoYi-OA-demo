package com.ruoyi.oa.contacts.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.oa.contacts.domain.OaContactPerson;
import com.ruoyi.oa.contacts.mapper.OaContactPersonMapper;
import com.ruoyi.oa.contacts.service.IOaContactsService;

/**
 * 通讯录Service业务层处理
 * 
 * @author oa
 */
@Service
public class OaContactsServiceImpl implements IOaContactsService
{
    @Autowired
    private OaContactPersonMapper contactPersonMapper;

    /**
     * 查询通讯录人员列表
     */
    @Override
    public List<OaContactPerson> selectContactList(OaContactPerson contact)
    {
        return contactPersonMapper.selectOaContactPersonList(contact);
    }

    /**
     * 查询通讯录人员详细
     */
    @Override
    public OaContactPerson selectContactById(Long personId)
    {
        return contactPersonMapper.selectOaContactPersonById(personId);
    }

    /**
     * 新增通讯录人员
     */
    @Override
    public int insertContact(OaContactPerson contact)
    {
        return contactPersonMapper.insertOaContactPerson(contact);
    }

    /**
     * 修改通讯录人员
     */
    @Override
    public int updateContact(OaContactPerson contact)
    {
        return contactPersonMapper.updateOaContactPerson(contact);
    }

    /**
     * 删除通讯录人员
     */
    @Override
    public int deleteContactByIds(Long[] personIds)
    {
        return contactPersonMapper.deleteOaContactPersonByIds(personIds);
    }
}