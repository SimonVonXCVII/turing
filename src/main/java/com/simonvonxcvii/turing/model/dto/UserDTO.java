package com.simonvonxcvii.turing.model.dto;

import com.simonvonxcvii.turing.model.query.PageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class UserDTO extends PageQuery {
    /**
     * 用户姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    public String name;
    /**
     * 用户手机号
     */
    @NotBlank(message = "用户手机号不能为空")
    public String mobile;
    /**
     * 用户性别
     */
    @NotBlank(message = "用户性别不能为空")
    public String gender;
    /**
     * 单位 id
     */
    @NotBlank(message = "单位 id 不能为空")
    public Integer orgId;
    /**
     * 单位名称
     */
    public String orgName;
    /**
     * 部门
     */
    public String department;
    /**
     * 登录账号
     */
    @NotBlank(message = "登录账号不能为空")
    public String username;
    /**
     * 是否已过期
     */
    public Boolean accountNonExpired;
    /**
     * 是否已锁定
     */
    public Boolean accountNonLocked;
    /**
     * 是否凭证已过期
     */
    public Boolean credentialsNonExpired;
    /**
     * 是否启用
     */
    public Boolean enabled;
    /**
     * 是否单位管理员
     */
    public Boolean manager;
    /**
     * 是否需要重新设置密码
     */
    public Boolean needSetPassword;
    /**
     * 用户角色
     */
    public Collection<RoleDTO> authorities;
    /**
     * 用户 id
     */
    private Integer id;
    /**
     * 创建日期
     */
    private LocalDateTime createdDate;
    /**
     * 角色集合
     */
    @NotEmpty(message = "角色集合不能为空")
    private List<Integer> roleList;
}
