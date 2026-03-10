package com.example.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.ecommerce.service.impl.CustomUserDetailsService;

/**
 * Configuration de la sécurité Spring Security.
 * Gère l'authentification, les autorisations et les redirections après login.
 * 
 * @author Equipe E-Commerce
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configuration de la chaîne de filtres de sécurité.
     * Définit les règles d'accès aux différentes URLs.
     * 
     * @param http Configuration HTTP
     * @return SecurityFilterChain configuré
     * @throws Exception en cas d'erreur
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF pour les formulaire Thymeleaf
            .csrf(csrf -> csrf.disable())
            
            // Configuration des autorisations
            .authorizeHttpRequests(auth -> auth
                // Routes publiques
                .requestMatchers("/", "/index", "/login", "/register", "/h2-console/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                
                // Routes admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Routes client (authentifié)
                .requestMatchers("/client/**", "/cart/**").hasRole("CLIENT")
                
                // Autres routes accessibles à tous
                .anyRequest().permitAll()
            )
            
            // Configuration du formulaire de login
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            // Configuration du logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // Configuration pour H2 Console
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }

    /**
     * Encodeur de mot de passe utilisant BCrypt.
     * 
     * @return PasswordEncoder configuré
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provider d'authentification DAO.
     * 
     * @return DaoAuthenticationProvider configuré
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Gestionnaire de redirection après authentification réussie.
     * Redirige vers la page appropriée selon le rôle de l'utilisateur.
     * 
     * @return AuthenticationSuccessHandler configuré
     */
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/homeAdmin");
            } else if (role.equals("ROLE_CLIENT")) {
                response.sendRedirect("/client/homePatisserie");
            } else {
                response.sendRedirect("/");
            }
        };
    }
}

