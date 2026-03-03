package com.example.ecommerce.config;

import com.example.ecommerce.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomUserDetailsService userDetailsService
    ) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/register").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/client/**").hasRole("CLIENT")
                    .anyRequest().authenticated()
            )

            .userDetailsService(userDetailsService)

            .formLogin(form -> form
                    .loginPage("/login")
                    .successHandler((request, response, authentication) -> {

                        boolean isAdmin = authentication.getAuthorities()
                                .stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                        if (isAdmin) {
                            response.sendRedirect("/admin/homeAdmin");
                        } else {
                            response.sendRedirect("/client/homeUser");
                        }
                    })
                    .permitAll()
            )

            .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            );

        return http.build();
    }
}