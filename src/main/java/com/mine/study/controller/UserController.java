package com.mine.study.controller;

import com.mine.study.bean.User;
import com.mine.study.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Slf4j
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/")
    public void testMethod() {
        // 测试信息提取
        // 不订阅, 不会实际发出请求, 但会进入我们的代理类
        // userApi.getAllUser();
        // userApi.getUserById("11111111");
        // userApi.deleteUserById("222222222");
        // userApi.createUser(
        // Mono.just(User.builder().name("xfq").age(33).build()));

        // 直接调用调用 实现调用rest接口的效果
        Flux<User> users = userService.getAllUser();
        users.subscribe(System.out::println);

        final String id = "5ac7d44714fb94522c5dde9c";
        userService.getUserById(id).subscribe(u -> {
            log.info("找到用户:" + u);
        }, e -> {
            log.error("找不到用户 id={}:{}", id, e.getMessage());
        });

        // 创建用户
        userService.createUser(Mono.just(User.builder().name("晓风轻").age(33).build()))
                .subscribe(System.out::println);

    }
}
