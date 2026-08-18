package com.example.apiGateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * this filter will be triggered before any route execution --> assign this to gateway route
 */
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        //exchange: read http header from server http request object
        return (exchange, chain) -> {
            ServerHttpRequest requestObject = exchange.getRequest();
            if (!requestObject.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header ", HttpStatus.UNAUTHORIZED);
            }

            String authHeader =Objects.requireNonNull(requestObject.getHeaders().get(HttpHeaders.AUTHORIZATION)).getFirst();
            String jwt=authHeader.replace("Bearer","");
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        //config properties
    }


}
