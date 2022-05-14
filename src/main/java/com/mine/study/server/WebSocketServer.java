package com.mine.study.server;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java 原生版 javax.websocket.Session:
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    // 静态变量，用来记录当前在线连接数
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
    /**
     * 固定前缀
     */
    private static final String USER_ID = "user_id_";


    /**
     * 获取在线连接数
     *
     * @return
     */
    public static int getOnlineCount() {
        return onlineCount.get();
    }

    /**
     * 在线连接加1
     */
    public static void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    /***
     * 在线连接数减1
     */
    public static void subOnlineCount() {
        onlineCount.decrementAndGet();
    }


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 根据 token 获取对应的 userId
        Object loginId = StpUtil.getLoginIdByToken(token);
        if (loginId == null) {
            session.close();
            throw new SaTokenException("连接失败，无效Token:" + token);
        }

        // put到集合，方便后续操作
        long userId = SaFoxUtil.getValueByType(loginId, Long.class);
        sessionMap.put(USER_ID + userId, session);
        //在线数加1
        addOnlineCount();
        String tips = "Web-Socket 连接成功，sid=" + session.getId() + "，userId=" + userId;
        System.out.println(tips);
        sendMessage(session, tips);
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        subOnlineCount();           //在线数减1
        System.out.println("连接关闭，sid=" + session.getId());
        for (String key : sessionMap.keySet()) {
            if (sessionMap.get(key).getId().equals(session.getId())) {
                sessionMap.remove(key);
            }
        }

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @ Param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("sid为：" + session.getId() + "，发来：" + message);
    }

    /**
     * @ Param session
     * @ Param error
     */
    @OnError
    public void onError(Session session, Throwable e) {
        System.out.println("sid为：" + session.getId() + "，发生错误");
        e.printStackTrace();
    }

    // 向指定客户端推送消息
    public static void sendMessage(Session session, String message) {
        try {
            System.out.println("向sid为：" + session.getId() + "，发送：" + message);
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 向指定用户推送消息
    public static void sendMessage(long userId, String message) {
        Session session = sessionMap.get(USER_ID + userId);
        if (session != null) {
            sendMessage(session, message);
        }
    }
}
