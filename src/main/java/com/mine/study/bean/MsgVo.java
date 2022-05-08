package com.mine.study.bean;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MsgVo<T> {
    /**
     * 发送方
     */
    private String from;

    /**
     * 接收方
     */
    private String to;

    /**
     * 时间
     */
    private LocalDateTime time = LocalDateTime.now();

    /**
     * 平台来源
     */
    private String platform;
    /**
     * 主题通道
     */
    private String topicChannel;

    /**
     * 信息业务对象
     */
    private T data;
}
