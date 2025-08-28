package com.soyeon.sharedcalendar.security.config;

import com.soyeon.sharedcalendar.security.oauth2.CustomOAuth2UserService;
import com.soyeon.sharedcalendar.security.filter.JwtCookieAuthenticationFilter;
import com.soyeon.sharedcalendar.security.handler.OAuth2AuthenticationFailureHandler;
import com.soyeon.sharedcalendar.security.handler.OAuth2AuthenticationSuccessHandler;
import com.soyeon.sharedcalendar.security.oauth2.CustomOidcUserService;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomOAuth2UserService customOAuth2UserService,
                                                   CustomOidcUserService customOidcUserService,
                                                   OAuth2AuthenticationSuccessHandler successHandler,
                                                   OAuth2AuthenticationFailureHandler failureHandler,
                                                   JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter
                                               ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://127.0.0.1:5173"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);
                return config;
            }))
            .addFilterBefore(jwtCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/oauth/**, /auth/refresh, /auth/kakao/init").permitAll();
                auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .anyRequest().authenticated();
            })
            .sessionManagement(session -> {
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            })
            .oauth2Login(oauth -> oauth
                    .userInfoEndpoint(ui -> ui
                            .userService(customOAuth2UserService)
                            .oidcUserService(customOidcUserService)
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
