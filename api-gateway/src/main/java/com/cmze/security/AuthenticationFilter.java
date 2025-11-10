package com.cmze.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouterValidator routerValidator;
    private final JwtValidator jwtValidator;

    public AuthenticationFilter(RouterValidator routerValidator, JwtValidator jwtValidator) {
        super(Config.class);
        this.routerValidator = routerValidator;
        this.jwtValidator = jwtValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (routerValidator.isSecured.test(exchange.getRequest())) {

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return this.onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
                }
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return this.onError(exchange, "Invalid authorization header", HttpStatus.UNAUTHORIZED);
                }
                String token = authHeader.substring(7);

                try {
                    if (!jwtValidator.validateToken(token)) {
                        return this.onError(exchange, "Token is invalid or expired", HttpStatus.UNAUTHORIZED);
                    }

                    Claims claims = jwtValidator.getAllClaimsFromToken(token);
                    String username = claims.getSubject();
                    List<String> roles = claims.get("roles", List.class);

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .headers(httpHeaders -> {
                                httpHeaders.set("X-Authenticated-User", username);
                                httpHeaders.set("X-User-Roles", String.join(",", roles));
                                httpHeaders.remove(HttpHeaders.AUTHORIZATION);
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());

                } catch (Exception e) {
                    return this.onError(exchange, "Token validation error", HttpStatus.UNAUTHORIZED);
                }
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
    }
}