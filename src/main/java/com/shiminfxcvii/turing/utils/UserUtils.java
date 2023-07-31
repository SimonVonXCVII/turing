package com.shiminfxcvii.turing.utils;

import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.common.result.ResultCode;
import com.shiminfxcvii.turing.entity.User;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * 用户工具类
 *
 * @author ShiminFXCVII
 * @since 12/23/2022 3:52 PM
 */
public class UserUtils {

    private static final SecurityContextHolderStrategy STRATEGY = SecurityContextHolder.getContextHolderStrategy();

    /**
     * 获取当前用户 id
     *
     * @return 当前用户 id 或者 null
     * @author ShiminFXCVII
     * @since 12/23/2022 3:53 PM
     */
    @Nullable
    public static String getUserId() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        return null;
    }

    /**
     * 获取当前用户的单位 id
     *
     * @return 当前用户的单位 id 或者 null
     * @author ShiminFXCVII
     * @since 12/23/2022 3:53 PM
     */
    @Nullable
    public static String getOrgId() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user.getOrgId();
        }
        return null;
    }

    /**
     * 获取当前用户详情
     *
     * @return Optional 封装后的当前用户详情
     * @author ShiminFXCVII
     * @since 12/23/2022 3:53 PM
     */
    public static Optional<UserDetails> getUserDetails() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        if (authentication.getDetails() instanceof UserDetails userDetails) {
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }

    /**
     * 获取当前用户详情或者抛出异常
     *
     * @return 当前用户详情或者抛出异常
     * @author ShiminFXCVII
     * @since 12/24/2022 11:59 AM
     */
    public static UserDetails getUserDetailsOrElseThrow() {
        return getUserDetails().orElseThrow(() -> BizRuntimeException.from(ResultCode.ERROR, "当前没有登录用户"));
    }

    /**
     * 获取当前用户信息
     *
     * @return Optional 封装后的当前用户信息
     * @author ShiminFXCVII
     * @since 12/23/2022 3:53 PM
     */
    public static Optional<User> getUser() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof User user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * 获取当前用户信息或者抛出异常
     *
     * @return 当前用户信息或者抛出异常
     * @author ShiminFXCVII
     * @since 12/24/2022 11:59 AM
     */
    public static User getUserOrElseThrow() {
        return getUser().orElseThrow(() -> BizRuntimeException.from(ResultCode.ERROR, "当前没有用户"));
    }

    /**
     * 判断当前是否是匿名用户（测试中...）
     *
     * @return true 或者 false
     * @author ShiminFXCVII
     * @since 1/9/2023 5:26 PM
     */
    public static boolean isAnonymous() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return true;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return true;
        }
        return !authentication.isAuthenticated();
    }

}