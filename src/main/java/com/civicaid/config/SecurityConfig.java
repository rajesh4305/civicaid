package com.civicaid.config;

import com.civicaid.security.CustomUserDetailsService;
import com.civicaid.security.jwt.JwtAuthenticationEntryPoint;
import com.civicaid.security.jwt.JwtAuthenticationFilter;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;

    private static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(PUBLIC_URLS).permitAll()

                // Citizen self-service
                .requestMatchers(HttpMethod.POST, "/citizens/register").hasRole("CITIZEN")
                .requestMatchers(HttpMethod.GET, "/citizens/me").hasRole("CITIZEN")
                .requestMatchers(HttpMethod.POST, "/applications").hasRole("CITIZEN")
                .requestMatchers(HttpMethod.GET, "/applications/my").hasRole("CITIZEN")

                // Welfare Officer
                .requestMatchers("/applications/*/eligibility").hasAnyRole("WELFARE_OFFICER", "ADMINISTRATOR")
                .requestMatchers("/applications/*/status").hasAnyRole("WELFARE_OFFICER", "ADMINISTRATOR")
                .requestMatchers("/disbursements/**").hasAnyRole("WELFARE_OFFICER", "ADMINISTRATOR")

                // Program Manager
                .requestMatchers("/programs/**").hasAnyRole("PROGRAM_MANAGER", "ADMINISTRATOR")
                .requestMatchers("/schemes/**").hasAnyRole("PROGRAM_MANAGER", "ADMINISTRATOR")

                // Compliance Officer
                .requestMatchers("/compliance/**").hasAnyRole("COMPLIANCE_OFFICER", "ADMINISTRATOR")
                .requestMatchers("/audits/**").hasAnyRole("COMPLIANCE_OFFICER", "GOVERNMENT_AUDITOR", "ADMINISTRATOR")

                // Reports
                .requestMatchers("/reports/**").hasAnyRole("PROGRAM_MANAGER", "ADMINISTRATOR", "GOVERNMENT_AUDITOR")

                // Admin only
                .requestMatchers("/users/**").hasRole("ADMINISTRATOR")
                .requestMatchers("/audit-logs/**").hasRole("ADMINISTRATOR")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
