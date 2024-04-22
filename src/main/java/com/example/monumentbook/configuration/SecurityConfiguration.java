package com.example.monumentbook.configuration;

import com.example.monumentbook.enums.Role;
import com.example.monumentbook.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@CrossOrigin
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui-html",
                                        "/api/v1/auth/**",
                                        "/api/v1/file/**",
                                        "api/v1/category/*",
                                        "api/v1/image/*",
                                        "api/v1/author/**",
                                        "/api/v1/book/**",
                                        "/api/v1/order/*",
                                        "/api/v1/news/*",
                                        "/api/v1/bookmarks/*",
                                        "/api/v1/search/**",
                                        "/api/v1/supplier/**",
                                        "/api/v1/card/**",
                                        "/api/v1/cart/*",
                                        "/api/v1/purchase/**"
                                        )
                        .permitAll()
//                                .requestMatchers("/api/v1/book/*").hasAnyAuthority(Role.USER.toString(),Role.ADMIN.toString())
                                .requestMatchers("/api/v1/user/*").hasAnyAuthority(Role.USER.toString(),Role.ADMIN.toString())
                                .anyRequest()
                                .authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

}
