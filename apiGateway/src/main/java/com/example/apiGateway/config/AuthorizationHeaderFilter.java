package com.example.apiGateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.security.auth.Subject;
import java.util.Base64;
import java.util.Objects;

/**
 * this filter will be triggered before any route execution --> assign this to gateway route
 */
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Autowired
    Environment environment;

    public AuthorizationHeaderFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //exchange: read http header from server http request object
        return (exchange, chain) -> {
            ServerHttpRequest requestObject = exchange.getRequest();
            if (!requestObject.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header ", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = Objects.requireNonNull(requestObject.getHeaders().get(HttpHeaders.AUTHORIZATION)).getFirst();
            String jwt = authHeader.replace("Bearer", "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "Jwt token is not valid", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {
        boolean isValid = true;

        String subject = null;
        String tokenSecret = environment.getProperty("token.secret");
        assert tokenSecret != null;
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        JwtParser jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();

        //reading values from claims
        try {
            Claims claims = jwtParser.parseSignedClaims(jwt).getPayload();
            subject = (String) claims.get("sub");
        } catch (Exception ex) {
            isValid = false;
        }
        if (subject == null || subject.isEmpty()) {
            isValid = false;
        }
        return isValid;
    }

    public static class Config {
        //config properties
    }


}
