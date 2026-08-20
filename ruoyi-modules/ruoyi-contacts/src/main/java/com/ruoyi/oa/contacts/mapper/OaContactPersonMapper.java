package com.ruoyi.oa.contacts.mapper;

import java.util.List;
import com.ruoyi.oa.contacts.domain.OaContactPerson;

/**
 * 通讯录人员Mapper接口
 * 
 * @author oa
 */
public interface OaContactPersonMapper
{
    /**
     * 查询通讯录人员
     */
    public OaContactPerson selectOaContactPersonById(Long personId);

    /**
     * 查询通讯录人员列表
     */
    public List<OaContactPerson> selectOaContactPersonList(OaContactPerson oaContactPerson);

    /**
     * 新增通讯录人员
     */
    public int insertOaContactPerson(OaContactPerson oaContactPerson);

    /**
     * 修改通讯录人员
     */
    public int updateOaContactPerson(OaContactPerson oaContactPerson);

    /**
     * 删除通讯录人员
     */
    public int deleteOaContactPersonById(Long personId);

    /**
     * 批量删除通讯录人员
     */
    public int deleteOaContactPersonByIds(Long[] personIds);
}