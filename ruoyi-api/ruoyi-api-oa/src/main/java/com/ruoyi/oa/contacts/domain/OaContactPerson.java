package com.ruoyi.oa.contacts.domain;

import com.ruoyi.common.core.web.domain.BaseEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 通讯录人员对象 oa_contact_person
 * 
 * @author oa
 */
public class OaContactPerson extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 人员ID */
    private Long personId;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名不能超过64个字符")
    private String name;

    /** 部门ID */
    @NotNull(message = "部门不能为空")
    @Positive(message = "部门ID必须为正数")
    private Long deptId;

    /** 部门名称 */
    @Size(max = 64, message = "部门名称不能超过64个字符")
    private String deptName;

    /** 岗位 */
    @Size(max = 64, message = "岗位不能超过64个字符")
    private String post;

    /** 手机号 */
    @Size(max = 32, message = "手机号不能超过32个字符")
    private String phone;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱不能超过128个字符")
    private String email;

    /** 办公电话 */
    @Size(max = 32, message = "办公电话不能超过32个字符")
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
