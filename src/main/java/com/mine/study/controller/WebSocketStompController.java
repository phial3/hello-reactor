package com.mine.study.controller;

import com.mine.study.bean.MsgVo;
import com.mine.study.service.MyWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/web/socket/stomp")
public class WebSocketStompController {

    @Resource
    MyWebSocketService myWebSocketService;

    /**
     * 发送信息 stomp
     * @return 统一出参
     */
    @PostMapping("/sendStompMsg")
    @MessageMapping("/sendStompMsg")
    public Object sendStompMsg(@RequestBody MsgVo msgVo) {
        log.info("--发送信息--");
        return myWebSocketService.sendStompMsg(msgVo);
    }

}