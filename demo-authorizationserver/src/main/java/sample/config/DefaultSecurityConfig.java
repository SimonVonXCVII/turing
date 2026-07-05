/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import sample.federation.FederatedIdentityAuthenticationSuccessHandler;

/**
 * @author Joe Grandja
 * @author Steve Riesenberg
 * @since 1.1
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

    // @formatter:off
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize ->
				authorize
					.requestMatchers("/assets/**", "/login").permitAll()
					.anyRequest().authenticated()
			)
			.formLogin(formLogin ->
				formLogin
					.loginPage("/login")
			)
			.oauth2Login(oauth2Login ->
				oauth2Login
					.loginPage("/login")
					.successHandler(authenticationSuccessHandler())
			);

		return http.build();
	}
	// @formatter:on

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new FederatedIdentityAuthenticationSuccessHandler();
    }

    // @formatter:off
	@Bean
	public UserDetailsService users(JdbcClient jdbcClient) {
		return username -> {
            com.simonvonxcvii.turing.common.entity.User user = jdbcClient.sql("SELECT u.* FROM public.turing_user u WHERE u.username = :username")
                    .param("username", username)
                    .query(com.simonvonxcvii.turing.common.entity.User.class)
                    .optional()
                    .orElseThrow(() -> UsernameNotFoundException.fromUsername(username));

            // 获取用户角色数据
//        val roleList = user.userRoles.map(UserRole::role).toList()

            return User.withDefaultPasswordEncoder()
                    .username(username)
//                    .password(user.getPassword())
            		.password("123456")
//            .passwordEncoder {
//                PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(it)
//            }
//            .roles(*roleList.map(Role::name).toTypedArray())
                    .roles("ADMIN")
//                    .authorities("ROLE_ADMIN")
//                    .accountExpired(!user.isAccountNonExpired())
//                    .accountLocked(!user.isAccountNonLocked())
//                    .credentialsExpired(!user.isCredentialsNonExpired())
//                    .disabled(!user.isEnabled())
                    .build();
        };

//		UserDetails user = User.withDefaultPasswordEncoder()
//				.username("user1")
//				.password("password")
//				.roles("USER")
//				.build();
//		return new InMemoryUserDetailsManager(user);
	}
	// @formatter:on

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}
