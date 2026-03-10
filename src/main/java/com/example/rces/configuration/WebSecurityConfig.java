package com.example.rces.configuration;

import com.example.rces.service.impl.WebSecurityService;
import com.example.rces.utils.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final WebSecurityService webSecurityService;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final AppUtil appUtil;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public WebSecurityConfig(WebSecurityService webSecurityService,
                             CustomAuthenticationProvider customAuthenticationProvider,
                             AppUtil appUtil,
                             JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.webSecurityService = webSecurityService;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.appUtil = appUtil;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/get-data/**", "/login", "/ws/**", "/api/auth/login").permitAll()
                        .requestMatchers("/home").hasAnyAuthority("TECHNOLOGIST", "OTK", "CONSTRUCTOR", "ADMIN", "MASTER")
                        .requestMatchers("/admin", "/registration").hasAuthority("ADMIN")
                        .requestMatchers("/create", "/requestslist/**")
                        .hasAnyAuthority("MASTER", "ADMIN", "CONSTRUCTOR", "TECHNOLOGIST", "OTK", "CONTROL")
                        .requestMatchers("/sgi/**").hasAnyAuthority("ADMIN", "CONTROL", "EVENT")
                        .requestMatchers(new TypeBasedRequestMatcher(webSecurityService)).authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .httpBasic(withDefaults())
                .logout(logout -> logout
                        .addLogoutHandler(appUtil)
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }
}