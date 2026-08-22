package com.example.ConfigServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Objects;

@Configuration
public class SecurityConfig {

    private final Environment environment;

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //authentication for each user endpoints - Basic authentication
        //restricting actuator endpoint to ADMIN role
        //get role allowed for CLIENT role
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/actuator/busrefresh").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/**").hasRole("CLIENT")
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/actuator/busrefresh"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    //creating users
    @Bean
    InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User
                .withUsername(Objects.requireNonNull(environment.getProperty("spring.security.user.name")))
                .password(passwordEncoder.encode(environment.getProperty("spring.security.user.password")))
                .roles(Objects.requireNonNull(environment.getProperty("spring.security.user.roles")))
                .build();

        UserDetails client = User
                .withUsername(Objects.requireNonNull(environment.getProperty("my-spring.security.user.name")))
                .password(passwordEncoder.encode(environment.getProperty("my-spring.security.user.password")))
                .roles(Objects.requireNonNull(environment.getProperty("my-spring.security.user.roles")))
                .build();

        return new InMemoryUserDetailsManager(admin,client);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
