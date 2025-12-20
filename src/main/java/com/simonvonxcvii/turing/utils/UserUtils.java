package com.simonvonxcvii.turing.utils;

import com.simonvonxcvii.turing.entity.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import java.util.Collection;

/**
 * 用户工具类
 *
 * @author Simon Von
 * @since 12/23/2022 3:52 PM
 */
public class UserUtils {

    private static final SecurityContextHolderStrategy STRATEGY = SecurityContextHolder.getContextHolderStrategy();
    private static final User USER = new User();

    /**
     * 获取当前用户信息或者抛出异常
     *
     * @return 当前用户信息
     * @author Simon Von
     * @since 12/24/2022 11:59 AM
     */
    public static User getUser() {
        if (getPrincipal() instanceof User user) {
            return user;
        }
        return USER;
    }

    /**
     * 获取当前用户 id
     *
     * @return 当前用户 id
     * @author Simon Von
     * @since 12/23/2022 3:53 PM
     */
    public static Integer getId() {
        return getUser().getId();
    }

    /**
     * 获取当前用户的单位 id
     *
     * @return 当前用户的单位 id
     * @author Simon Von
     * @since 12/23/2022 3:53 PM
     */
    public static Integer getOrgId() {
        return getUser().getOrgId();
    }

    /**
     * 当前用户是否是超级管理员
     *
     * @return 当前用户是否是超级管理员
     * @author Simon Von
     * @since 2023/8/25 16:06
     */
    public static boolean isSuper() {
        return getUser().getRoles().contains("Super");
    }

    /**
     * 当前用户是否是管理员
     *
     * @return 当前用户是否是管理员
     * @author Simon Von
     * @since 2023/8/25 16:06
     */
    public static boolean isAdmin() {
        return getUser().getRoles().contains("Admin");
    }

    /**
     * 当前用户是否是用户
     *
     * @return 当前用户是否是用户
     * @author Simon Von
     * @since 12/20/25 8:37AM
     */
    public static boolean isUser() {
        return getUser().getRoles().contains("User");
    }

    /**
     * 由 AuthenticationManager 设置以指示主体已被授予的权限。 请注意，类不应依赖此值作为有效值，除非它已由受信任的 AuthenticationManager 设置。
     * 实现应确保对返回的集合数组的修改不会影响 Authentication 对象的状态，或使用不可修改的实例。
     *
     * @return 授予主体的权限，如果令牌尚未经过身份验证，则为空集合。 永不为空。
     * @author Simon Von
     * @since 2023/8/31 14:46
     */
    public static Collection<? extends GrantedAuthority> getAuthorities() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getAuthorities();
    }

    /**
     * 证明委托人正确的凭据。 这通常是密码，但也可以是与 AuthenticationManager 相关的任何内容。 调用者需要填写凭据。
     *
     * @return 证明委托人身份的凭证
     * @author Simon Von
     * @since 2023/8/31 14:47
     */
    public static Object getCredentials() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getCredentials();
    }

    /**
     * 存储有关身份验证请求的其他详细信息。 这些可能是 IP 地址、证书序列号等。
     *
     * @return 有关身份验证请求的其他详细信息，如果未使用则为 null
     * @author Simon Von
     * @since 2023/8/31 14:48
     */
    public static Object getDetails() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getDetails();
    }

    /**
     * 正在验证的主体的身份。 如果使用用户名和密码进行身份验证请求，则这将是用户名。 调用者需要填充身份验证请求的主体。
     * AuthenticationManager 实现通常会返回包含更丰富信息的身份验证，作为应用程序使用的主体。 许多身份验证提供程序将创建一个 UserDetails 对象作为主体。
     *
     * @return 正在认证的主体或认证后已认证的主体。
     * @author Simon Von
     * @since 2023/8/31 14:49
     */
    public static Object getPrincipal() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getPrincipal();
    }

    /**
     * 用于指示 AbstractSecurityInterceptor 是否应向 AuthenticationManager 提供身份验证令牌。
     * 通常，AuthenticationManager（或更常见的是其 AuthenticationProvider 之一）将在成功身份验证后返回一个不可变的身份验证令牌，
     * 在这种情况下，该令牌可以安全地为此方法返回 true。 返回 true 将提高性能，因为不再需要为每个请求调用 AuthenticationManager。
     * 出于安全原因，此接口的实现应该非常小心地从该方法返回 true，除非它们是不可变的，或者有某种方法确保属性自最初创建以来没有发生更改。
     *
     * @return true 如果令牌已通过身份验证并且 AbstractSecurityInterceptor 不需要再次将令牌提供给 AuthenticationManager 进行重新身份验证。
     * @author Simon Von
     * @since 2023/8/31 14:49
     */
    public static boolean isAuthenticated() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    /**
     * 判断当前是否是匿名用户（测试中...）
     *
     * @return true：如果是匿名用户，false：不是匿名用户
     * @author Simon Von
     * @since 1/9/2023 5:26 PM
     */
    public static boolean isAnonymous() {
        Authentication authentication = STRATEGY.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication instanceof AnonymousAuthenticationToken;
    }

}
