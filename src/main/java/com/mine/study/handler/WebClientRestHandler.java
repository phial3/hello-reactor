package com.mine.study.handler;

import com.mine.study.bean.MethodInfo;
import com.mine.study.bean.ServerInfo;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientRestHandler implements RestHandler {
    private WebClient client;
    private WebClient.RequestBodySpec request;

    public WebClientRestHandler(ServerInfo serverInfo) {
        this.client = WebClient.create(serverInfo.getUrl());
    }

    /**
     * 处理rest请求
     */
    @Override
    public Object invokeRest(MethodInfo methodInfo) {
        // 返回结果
        Object result = null;

        request = this.client
                // 请求方法
                .method(methodInfo.getMethod())
                // 请求url 和 参数
                .uri(methodInfo.getUrl(), methodInfo.getParams())
                // contentType
                .accept(MediaType.APPLICATION_JSON);

        WebClient.ResponseSpec retrieve = null;

        // 判断是否带了body
        if (methodInfo.getBody() != null) {
            // 发出请求
            retrieve = request.body(methodInfo.getBody(), methodInfo.getBodyElementType()).retrieve();
        } else {
            retrieve = request.retrieve();
        }

        // 处理异常
        retrieve.onStatus(status -> status.value() == 404, resp -> Mono.just(new RuntimeException("Not Found")));

        // 处理body
        if (methodInfo.isReturnFlux()) {
            result = retrieve.bodyToFlux(methodInfo.getReturnElementType());
        } else {
            result = retrieve.bodyToMono(methodInfo.getReturnElementType());
        }

        return result;
    }
}
