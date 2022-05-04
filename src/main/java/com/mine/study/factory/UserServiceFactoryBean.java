package com.mine.study.factory;

import com.mine.study.proxy.ProxyCreator;
import com.mine.study.service.UserService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFactoryBean implements FactoryBean<UserService> {
    private final ProxyCreator proxyCreator;

    public UserServiceFactoryBean(ProxyCreator proxyCreator) {
        this.proxyCreator = proxyCreator;
    }

    @Override
    public UserService getObject() throws Exception {
        return (UserService) proxyCreator.createProxy(this.getObjectType());
    }

    @Override
    public Class<?> getObjectType() {
        return UserService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
