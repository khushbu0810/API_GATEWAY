package com.example.apiGateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@Slf4j
public class MyPreFilter implements GlobalFilter {

    // global filter access details of each http request and
    // log the request path as well as name, value of each http request header.

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("My first PreFilter is executed.....");

        //request path
        String requestPath = exchange.getRequest().getPath().toString();
        log.info("Request Path = {}", requestPath);

        //request header
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Set<String> headerNames = headers.headerNames();
        headerNames.forEach((hName) -> {
            String hValue = headers.getFirst(hName);
            log.info("{} {}", hName, hValue);
        });

        return chain.filter(exchange);
    }
}
