package com.example.gatewayexample.filter;

import com.example.gatewayexample.domain.dto.RequestLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info(makeLog(exchange.getRequest()));
            RequestLogDto logDto = createLogDto(exchange.getRequest());
            // TODO : Kafka Topic Send -> 요청 정보 DB 저장
        })));
    }

//    private String getHeaderByHeaderName(String headerName, ServerHttpRequest request) {
//        return request.getHeaders().get(headerName).get(0);
//    }

    private RequestLogDto createLogDto(ServerHttpRequest request) {
        String requestPath = request.getPath().toString();
        String requestMethod = request.getMethod().toString();
        String requestClientAddress = request.getRemoteAddress().getHostString();
//        String contentType = getHeaderByHeaderName("Content-Type", request);
//        String userAgent = getHeaderByHeaderName("User-Agent", request);


        return RequestLogDto.builder()
                .requestPath(requestPath)
                .requestMethod(requestMethod)
                .requestClientAddress(requestClientAddress)
//                .contentType(contentType)
//                .userAgent(userAgent)
                .build();
    }

    static class Config {}

    private String makeLog(ServerHttpRequest request) {
        StringBuilder builder = new StringBuilder();
        String requestUri = request.getURI().toString();
        String requestMethod = request.getMethod().toString();

        return builder
                .append("Request URI = ").append(requestUri)
                .append("Request Method = ").append(requestMethod)
                .toString();
    }
}
