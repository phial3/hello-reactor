package com.mine.study.service;

import com.mine.study.bean.MsgVo;

public interface MyWebSocketService {
    Object sendStompMsg(MsgVo msgVo);
}
