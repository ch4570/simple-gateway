package com.example.gatewayexample.filter;

import com.example.gatewayexample.utils.AesUtils;
import com.example.gatewayexample.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtils jwtUtils;
    private final AesUtils aesUtils;

    public AuthenticationFilter(JwtUtils jwtUtils, AesUtils aesUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
        this.aesUtils = aesUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
       return (this::validateToken);
    }

    public static class Config {}

    private Mono<Void> validateToken(ServerWebExchange exchange,
                                     GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if (!request.getHeaders().containsKey("Authorization")) {
            return tokenIsNotAvailable(response, "토큰이 누락되었습니다. 확인 후 다시 시도해주시기 바랍니다.");
        }

        String token = decodeToken(request.getHeaders().get("Authorization").get(0));
        log.info("decodeToken = {}", token);

        if (!jwtUtils.verifyToken(token)) {
            return tokenIsNotAvailable(response, "유효하지 않은 토큰입니다. 확인 후 다시 시도해주시기 바랍니다.");
        }

        setCustomHeaders(request, "X-Authorization-Email", jwtUtils.getEmail(token));

        return chain.filter(exchange);
    }



    private void setCustomHeaders(ServerHttpRequest request, String headerName, String value) {
        request.mutate().header(headerName, aesUtils.enCodeString(value));
    }

    private String decodeToken(String token) {
        return aesUtils.decodeString(token);
    }

    private Mono<Void> tokenIsNotAvailable(ServerHttpResponse response, String message) {
        byte[] byteMessage = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(byteMessage);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Flux.just(buffer));
    }

}
