package com.mine.study.config;

import com.mine.study.proxy.JDKProxyCreator;
import com.mine.study.proxy.ProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfig {
    /**
     * 创建jdk代理工具类
     *
     * @return
     */
    @Bean
    ProxyCreator jdkProxyCreator() {
        return new JDKProxyCreator();
    }
}
