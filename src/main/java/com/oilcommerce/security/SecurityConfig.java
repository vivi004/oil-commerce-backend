package com.oilcommerce.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${cors.allowed-origins:http://localhost:4200,http://localhost:4300,http://localhost:4201,https://oil-commerce-frontend.onrender.com,https://*.onrender.com}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Auth & test endpoints
                .requestMatchers("/auth/**", "/test/**").permitAll()
                // Public read-only product/category endpoints
                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/brands/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tenants/**", "/admin/tenants/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/inventory/**", "/admin/inventory/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/platform-settings/**", "/admin/platform-settings/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/mandi-benchmarks/**", "/admin/mandi-benchmarks/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/reports/**", "/admin/reports/**").permitAll()
                // Administrative operational endpoints
                .requestMatchers(HttpMethod.POST, "/inventory/**", "/admin/inventory/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/tenants/**", "/admin/tenants/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/tenants/**", "/admin/tenants/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/tenants/**", "/admin/tenants/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/tenants/**", "/admin/tenants/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/brands/**", "/admin/brands/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/brands/**", "/admin/brands/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/brands/**", "/admin/brands/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/categories/**", "/admin/categories/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/categories/**", "/admin/categories/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/categories/**", "/admin/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/products/**", "/admin/products/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/products/**", "/admin/products/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/products/**", "/admin/products/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/platform-settings/**", "/admin/platform-settings/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/mandi-benchmarks/**", "/admin/mandi-benchmarks/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/mandi-benchmarks/**", "/admin/mandi-benchmarks/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/mandi-benchmarks/**", "/admin/mandi-benchmarks/**").permitAll()
                // Actuator health
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // Static files
                .requestMatchers("/files/**").permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
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
        return new BCryptPasswordEncoder(12);
    }
}
