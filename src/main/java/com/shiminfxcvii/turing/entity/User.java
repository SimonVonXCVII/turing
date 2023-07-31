package com.shiminfxcvii.turing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shiminfxcvii.turing.enums.GenderEnum;
import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-19 15:58:28
 */
@Accessors(chain = true)
@Getter
@Setter
@TableName("soil_user")
// TODO: 2023/6/25 public class User extends BaseEntity implements UserDetails {
public class User extends BaseEntity {

    /**
     * ES 索引名称
     */
    public static final String INDEX = "soil_user";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = INDEX + ":";

    /**
     * 用户姓名
     */
    public String nickName;

    /**
     * 用户手机号
     */
    public String mobile;

    /**
     * 登录账号
     */
    public String username;

    /**
     * 用户密码
     */
    public String password;

    /**
     * 是否需要重新设置密码
     */
    public Boolean needSetPassword;

    /**
     * 身份证
     */
    public String idCard;

    /**
     * 出生日期
     */
    public LocalDate birthday;

    /**
     * 用户性别
     */
    public GenderEnum gender;

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
     * 学历
     */
    public String education;

    /**
     * 是否单位管理员
     */
    public Boolean manager;

    /**
     * 是否锁定
     */
    public Boolean locked;

    /**
     * 是否已禁用
     */
    public Boolean disabled;

    /**
     * 是否是超级管理员
     */
    public Boolean admin;

    /**
     * 当前用户的 token
     *
     * @since 2023/4/11 18:07
     */
    public transient String token;

    /**
     * 用户角色集合
     *
     * @since 2023/4/11 18:07
     */
    public transient List<Role> roleList;

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