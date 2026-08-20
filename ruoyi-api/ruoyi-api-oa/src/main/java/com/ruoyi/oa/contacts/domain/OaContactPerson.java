package com.ruoyi.oa.contacts.domain;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 通讯录人员对象 contact_person
 * 
 * @author oa
 */
public class OaContactPerson extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 人员ID */
    private Long personId;

    /** 姓名 */
    private String name;

    /** 部门ID */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 岗位 */
    private String post;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 办公电话 */
    private String officePhone;

    public Long getPersonId()
    {
        return personId;
    }

    public void setPersonId(Long personId)
    {
        this.personId = personId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getPost()
    {
        return post;
    }

    public void setPost(String post)
    {
        this.post = post;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getOfficePhone()
    {
        return officePhone;
    }

    public void setOfficePhone(String officePhone)
    {
        this.officePhone = officePhone;
    }
}