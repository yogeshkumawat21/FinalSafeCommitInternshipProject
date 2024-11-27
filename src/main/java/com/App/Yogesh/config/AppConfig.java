package com.App.Yogesh.config;

import com.App.Yogesh.Models.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
public class AppConfig {

    @Autowired
    private JwtValidator jwtValidator;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Security Filter Chain");

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signin", "/auth/signup", "/forgetPassword", "/verifyMail/{email}", "/changePassword/{email}",
                                "/docs/**",  "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/oauth2/**")
                        .permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(new CustomOAuth2UserService()) // Your custom service
                        )
                        .successHandler((request, response, authentication) -> {
                            log.info("OAuth2 login successful, redirecting user");
                            // Handle redirection after successful login
                            String redirectUrl = (String) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                            if (redirectUrl == null) {
                                redirectUrl = "/home"; // Default fallback URL
                            }
                            response.sendRedirect(redirectUrl);
                        })
                        .defaultSuccessUrl("/home", true) // Fallback page after login
                )
                .formLogin(Customizer.withDefaults()) // Disable form login
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Manage sessions
                )
                .addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class) // Add JWT filter
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // CORS config

        log.info("Security Filter Chain successfully configured");
        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        log.debug("Configuring CORS policy");
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();
            cfg.setAllowedOrigins(Arrays.asList("http://localhost:8081")); // Allow only requests from this origin
            cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
            cfg.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers
            cfg.setExposedHeaders(Collections.singletonList("Authorization")); // Expose Authorization header
            cfg.setMaxAge(3600L); // Cache CORS preflight response for 1 hour
            return cfg;
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        log.info("Configuring Authentication Manager");
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Configuring Password Encoder (BCrypt)");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        log.info("Creating RestTemplate bean");
        return new RestTemplate();
    }
}
