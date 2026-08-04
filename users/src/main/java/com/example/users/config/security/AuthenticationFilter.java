package com.example.users.config.security;

import com.example.users.dto.LoginDTO;
import com.example.users.dto.UserDTO;
import com.example.users.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

/*
 * This filter is triggered when a user attempts to log in.
 * It reads the username and password from the request,
 * authenticates the user, and generates a JWT on successful authentication.
 * <p>
 * Authentication Manager  ->  Responsible for authenticating the user


LOGIN CRED ->
 Read JSON request body and convert it into LoginDTO
 Example JSON:
 {
 "email":"khushbu@example.com",
 "password":"Password123"
 }



authenticationToken -> Create an authentication request using email and password.
             At this point the user is NOT authenticated.

at end RETURN -> Send username and password to Spring Security.
                Spring Security will call loadUserByUsername()
                        to fetch the user from the database.
                It will then compare the entered password with
                        the encrypted password stored in the database.

 */


@RequiredArgsConstructor
@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final Environment environment;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDTO loginCred = new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginCred.getEmail(),
                            loginCred.getPassword(),
                            new ArrayList<>()
                    );
            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain, Authentication authentication) throws IOException, ServletException {

        //take authenticated user details and generate jwt token
        String username = ((User) Objects.requireNonNull(authentication.getPrincipal())).getUsername();

        UserDTO userDTO = userService.getUserByEmail(username);

        String tokenSecret = environment.getProperty("token.secret");

        assert tokenSecret != null;
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());

        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        // Generate JWT token
        String token = Jwts.builder()
                .setSubject(userDTO.getUserId())
                .setExpiration(new Date(
                        System.currentTimeMillis()
                                + Long.parseLong(Objects.requireNonNull(environment.getProperty("token.expiration_time")))))
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(secretKey)
                .compact();

        // Add JWT token to response header
        response.addHeader("token", token);
        response.addHeader("userId", userDTO.getUserId());
    }
}