package com.mine.study.handler;

import com.mine.study.bean.MethodInfo;

public interface RestHandler {
    /**
     * 调用rest请求, 返回接口
     *
     * @param methodInfo
     * @return
     */
    Object invokeRest(MethodInfo methodInfo);
}
