package com.soyeon.sharedcalendar.auth.security.config;

import com.soyeon.sharedcalendar.auth.security.CustomOAuthUserService;
import com.soyeon.sharedcalendar.auth.security.handler.OAuth2AuthenticationFailureHandler;
import com.soyeon.sharedcalendar.auth.security.handler.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomOAuthUserService userService,
                                                   OAuth2AuthenticationSuccessHandler successHandler,
                                                   OAuth2AuthenticationFailureHandler failureHandler
                                               ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/oauth/**, /auth/refresh, /auth/kakao/init").permitAll();
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(ui -> ui
                                .userService(userService)
                                .oidcUserService(userService)
                        )
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(Customizer.withDefaults());
                });
        return http.build();
    }
}
