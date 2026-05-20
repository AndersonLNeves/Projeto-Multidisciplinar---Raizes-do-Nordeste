package com.raizesnordeste.infrastructure.config;

import com.raizesnordeste.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.raizesnordeste.infrastructure.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    private static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/h2-console/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 console
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                // Cardápio público (GET)
                .requestMatchers(HttpMethod.GET, "/unidades/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
                // Gestão de produtos e unidades - GERENTE e ADMIN
                .requestMatchers(HttpMethod.POST, "/produtos/**").hasAnyRole("GERENTE", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/produtos/**").hasAnyRole("GERENTE", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/produtos/**").hasAnyRole("GERENTE", "ADMIN")
                .requestMatchers("/unidades/**").hasAnyRole("GERENTE", "ADMIN")
                // Estoque - GERENTE e ADMIN
                .requestMatchers("/estoque/**").hasAnyRole("GERENTE", "ADMIN")
                // Status de pedido pela cozinha
                .requestMatchers(HttpMethod.PATCH, "/pedidos/*/status").hasAnyRole("COZINHA", "ATENDENTE", "GERENTE", "ADMIN")
                // Auditoria - apenas ADMIN
                .requestMatchers("/audit/**").hasRole("ADMIN")
                // Qualquer outra rota requer autenticação
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
