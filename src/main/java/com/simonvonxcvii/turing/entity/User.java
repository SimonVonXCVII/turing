package com.simonvonxcvii.turing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author SimonVonXCVII
 * @since 2022-12-19 15:58:28
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_user")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_user SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
//@SQLDeleteAll()
@Where(clause = "deleted = FALSE")
//@RedisHash
//@Document(indexName = "turing_user")
public class User extends AbstractAuditable implements UserDetails {

    /**
     * ES 索引名称
     */
    public static final String ES_INDEX = "turing_user";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = ES_INDEX + ":";

    public static final String NAME = "name";
    public static final String MOBILE = "mobile";
    public static final String GENDER = "gender";
    public static final String ORG_ID = "orgId";
    public static final String ORG_NAME = "orgName";
    public static final String DEPARTMENT = "department";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ACCOUNT_NON_EXPIRED = "accountNonExpired";
    public static final String ACCOUNT_NON_LOCKED = "accountNonLocked";
    public static final String CREDENTIALS_NON_EXPIRED = "credentialsNonExpired";
    public static final String ENABLED = "enabled";
    public static final String MANAGER = "manager";
    public static final String NEED_SET_PASSWORD = "needSetPassword";

    /**
     * 用户姓名
     */
    public String name;
    /**
     * 用户手机号
     */
    public String mobile;
    /**
     * 用户性别
     */
    public String gender;
    /**
     * 组织机构 id
     */
    public String orgId;
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
    public String username;
    /**
     * 用户密码
     */
    public String password;
    /**
     * 是否账号没有过期
     */
    public boolean accountNonExpired;
    /**
     * 是否账号没有锁定
     */
    public boolean accountNonLocked;
    /**
     * 是否凭证没有过期
     */
    public boolean credentialsNonExpired;
    /**
     * 是否启用
     */
    public boolean enabled;
    /**
     * 是否单位管理员
     */
    public boolean manager;
    /**
     * 是否需要重置密码
     */
    public boolean needSetPassword;

    /**
     * 用户角色
     */
//    @Transient
    public transient Collection<Role> authorities;
    /**
     * 是否是超级管理员
     */
    public transient boolean admin;
    /**
     * 当前用户的 token
     *
     * @since 2023/4/11 18:07
     */
    public transient String token;
    /**
     * 用户所处的单位级别
     *
     * @since 2023/7/1 18:53
     */
    public transient String orgLevel;
    /**
     * 省（市、区）编码
     *
     * @since 2023/4/11 18:07
     */
    public transient Integer provinceCode;
    /**
     * 市（州、盟）编码
     *
     * @since 2023/7/1 18:53
     */
    public transient Integer cityCode;
    /**
     * 县（市、旗）编码
     *
     * @since 2023/7/1 18:53
     */
    public transient Integer districtCode;
    /**
     * 省（市、区）名称
     *
     * @since 2023/7/1 18:53
     */
    public transient String provinceName;
    /**
     * 市（州、盟）名称
     *
     * @since 2023/7/1 18:53
     */
    public transient String cityName;
    /**
     * 县（市、旗）名称
     *
     * @since 2023/7/1 18:53
     */
    public transient String districtName;
}
