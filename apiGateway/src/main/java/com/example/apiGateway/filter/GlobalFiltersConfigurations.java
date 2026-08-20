package com.example.apiGateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class GlobalFiltersConfigurations {

    //instead of passing as arguments, we pass as lambda expressions here .... and pre and post are working in same function...
    @Bean
    public GlobalFilter secondPreFilter() {
        return (exchange, chain) -> {
            log.info("Second pre-filter is executed......");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Second post-filter is executed.....");
            }));
        };
    }

    @Bean
    public GlobalFilter thirdPreFilter() {
        return (exchange, chain) -> {
            log.info("Third pre-filter is executed......");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Third post-filter is executed.....");
            }));
        };
    }
}
