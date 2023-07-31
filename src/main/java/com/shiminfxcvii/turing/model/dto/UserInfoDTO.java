package com.shiminfxcvii.turing.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户登录成功后所需要的信息
 *
 * @author ShiminFXCVII
 * @since 12/17/2022 4:52 PM
 */
@Schema(name = "用户登录成功后所需要的信息")
@Getter
@Setter
public class UserInfoDTO {

    /**
     * 用户 id
     */
    @Schema(description = "用户 id")
    private String id;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickName;
    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String mobile;
    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String avatarUrl;
    /**
     * 用户角色集合
     */
    @Schema(description = "用户角色集合")
    private List<String> roles;
    /**
     * 用户的访问令牌
     */
    @Schema(description = "用户的访问令牌")
    private String token;
    /**
     * 单位名称
     */
    @Schema(description = "单位名称")
    private String orgName;
    /**
     * 单位 id
     */
    @Schema(description = "单位 id")
    private String orgId;
    /**
     * 是否需要重新设置密码
     */
    @Schema(description = "是否需要重新设置密码")
    private Boolean needSetPassword;

}