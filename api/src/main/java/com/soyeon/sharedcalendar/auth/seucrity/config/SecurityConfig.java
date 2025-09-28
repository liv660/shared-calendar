package com.soyeon.sharedcalendar.seucrity.config;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.seucrity.SocialAuthenticationConverter;
import com.soyeon.sharedcalendar.seucrity.SocialAuthenticationSuccessHandler;
import com.soyeon.sharedcalendar.seucrity.SocialAuthenticationProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(AuthProperties.class)
public class SecurityConfig {
    private final SocialAuthenticationProvider authenticationProvider;
    private final SocialAuthenticationConverter authenticationConverter;

    public SecurityConfig(SocialAuthenticationProvider authenticationProvider, SocialAuthenticationConverter authenticationConverter) {
        this.authenticationProvider = authenticationProvider;
        this.authenticationConverter = authenticationConverter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager(), authenticationConverter);
        authFilter.setSuccessHandler((new SocialAuthenticationSuccessHandler()));
        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/**").permitAll()
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(csrf ->
                        csrf.ignoringRequestMatchers("/swagger-ui/**", "/v3/api-docs/**"))
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//    @Bean @Qualifier("googleJwtDecoder")
//    JwtDecoder jwtDecoder(@Value("${auth.google.jwks-uri}") String jwksUri,
//                          @Value("${auth.google.issure}") String issure,
//                          @Value("${auth.google.issure}") String clientId) {
//        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
//        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issure);
//        JwtClaimValidator<Object> withAudience = new JwtClaimValidator<>("aud", aud -> {
//            if (aud instanceof String s) return s.equals(clientId);
//            if (aud instanceof Collection<?> c) return c.contains(clientId);
//            return false;
//        });
//
//        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
//        return decoder;
//    }
}
