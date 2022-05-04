package com.mine.study.config;

import com.mine.study.proxy.JDKProxyCreator;
import com.mine.study.proxy.ProxyCreator;
import com.mine.study.service.UserService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    /**
     * 创建jdk代理工具类
     *
     * @return
     */
    @Bean
    ProxyCreator jdkProxyCreator() {
        return new JDKProxyCreator();
    }

    @Bean
    FactoryBean<UserService> userApi(ProxyCreator proxyCreator) {
        return new FactoryBean<UserService>() {

            @Override
            public Class<?> getObjectType() {
                return UserService.class;
            }

            /**
             * 返回代理对象
             */
            @Override
            public UserService getObject() throws Exception {
                return (UserService) proxyCreator.createProxy(this.getObjectType());
            }
        };
    }

}
