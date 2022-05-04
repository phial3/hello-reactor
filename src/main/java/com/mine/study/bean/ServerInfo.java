package com.mine.study.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {

    /**
     * 服务器url
     */
    private String url;

    /**
     * 服务器名称
     */
    private String name;

    /**
     * 服务器状态
     */
    private String status;

    /**
     * 服务器类型
     */
    private String type;

    /**
     * 服务器描述
     */
    private String description;
}
