package com.mine.study.interceptor;


import cn.dev33.satoken.stp.StpUtil;
import com.mine.study.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手的前置拦截器
 **/
@Slf4j
public class MyWebSocketInterceptor implements HandshakeInterceptor {
    /**
     * 握手之前触发 (return true 才会握手成功 )
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("---- 握手之前触发获取登录的token:" + StpUtil.getTokenValue());
        // 未登录情况下拒绝握手
        if(!StpUtil.isLogin()) {
            System.out.println("---- 未授权客户端，连接失败");
            return false;
        }
        // 标记 userId，握手成功
        attributes.put("userId", StpUtil.getLoginIdAsLong());
        return true;
    }

    public User getUser(ServerHttpRequest request) {
        User user = null;
        //获取token认证,解析token获取用户信息
        //鉴权，我的方法是，前端把token传过来，解析token，判断正确与否，return true表示通过，false请求不通过。
        ServletServerHttpRequest req = (ServletServerHttpRequest) request;
        String token = req.getServletRequest().getParameter("token");
        //TODO: 鉴权设置用户,解析到用户信息
        if (StringUtils.isNotBlank(token)) {
            user = new User();
        }
        return user;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        System.out.println("---- 握手之后触发 ");
    }
}


