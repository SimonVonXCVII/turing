package com.shiminfxcvii.turing.component;//package com.soil.component;
//
//import com.shiting.utils.soil.UserUtils;
//import org.springframework.security.authorization.AuthorizationDecision;
//import org.springframework.security.authorization.AuthorizationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
//import org.springframework.stereotype.Component;
//
//import java.util.function.Supplier;
//
///**
// * @author ShiminFXCVII
// * @since 3/10/2023 11:26 AM
// */
//@Component
//public class AuthorizationManagerImpl implements AuthorizationManager<RequestAuthorizationContext> {
//    /**
//     * Determines if access is granted for a specific authentication and object.
//     *
//     * @param authentication the {@link Supplier} of the {@link Authentication} to check
//     * @param object         the {@link RequestAuthorizationContext} object to check
//     * @return an {@link AuthorizationDecision} or null if no decision could be made
//     */
//    @Override
//    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
//        boolean granted = isGranted(authentication.get());
//        return new AuthorizationDecision(granted);
//    }
//
//    boolean isGranted(Authentication authentication) {
//        return authentication != null && !UserUtils.isAnonymous() && authentication.isAuthenticated();
//    }
//}