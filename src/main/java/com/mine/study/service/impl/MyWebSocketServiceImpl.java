package com.mine.study.service.impl;

import javax.annotation.Resource;

import cn.hutool.json.JSONUtil;
import com.mine.study.bean.MsgVo;
import com.mine.study.service.MyWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MyWebSocketServiceImpl implements MyWebSocketService {

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Object sendStompMsg(MsgVo webSocketMsgVo) {
        String topicChannel = webSocketMsgVo.getTopicChannel();
        if (StringUtils.isNotBlank(topicChannel)) {
            topicChannel = "/" + topicChannel;
        }

        String message = JSONUtil.toJsonStr(webSocketMsgVo);
        String to = webSocketMsgVo.getTo();
        try {
            if (StringUtils.isNotBlank(to)) {
                //MD 不明原因用convertAndSendToUser不能收到，确认订阅没有问题
                //simpMessagingTemplate.convertAndSendToUser(to, topicChannel, message);
                simpMessagingTemplate.convertAndSend(topicChannel + "/" + to, message);
            } else {
                simpMessagingTemplate.convertAndSend(topicChannel, message);
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
