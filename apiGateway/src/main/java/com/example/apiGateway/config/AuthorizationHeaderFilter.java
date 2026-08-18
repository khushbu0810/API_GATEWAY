package com.example.apiGateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 this filter will be triggered before any route execution --> assign this to gateway route
 */
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>{

    @Override
    public GatewayFilter apply(Config config) {
        return null;
    }

    public static class Config{
        //config properties
    }


}
