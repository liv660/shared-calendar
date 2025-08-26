package com.soyeon.sharedcalendar.auth.security.config;

import com.soyeon.sharedcalendar.auth.security.converter.OAuth2AuthenticationConverter;
import com.soyeon.sharedcalendar.auth.security.handler.OAuth2AuthenticationFailureHandler;
import com.soyeon.sharedcalendar.auth.security.OAuth2AuthenticationProvider;
import com.soyeon.sharedcalendar.auth.security.handler.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    AuthenticationManager authenticationManager(OAuth2AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   OAuth2AuthenticationConverter authenticationConverter,
                                                   OAuth2AuthenticationSuccessHandler successHandler,
                                                   OAuth2AuthenticationFailureHandler failureHandler
                                               ) throws Exception {
        AuthenticationFilter loginFilter = new AuthenticationFilter(authenticationManager, authenticationConverter);
        loginFilter.setRequestMatcher(req -> {
            boolean path = req.getServletPath().equals("/auth/login");
            boolean post = req.getMethod().equalsIgnoreCase("POST");
            return post && path;
        });
        loginFilter.setSuccessHandler(successHandler);
        loginFilter.setFailureHandler(failureHandler);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/**").permitAll();
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(Customizer.withDefaults());
                })
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
