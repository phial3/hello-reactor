package com.mine.study.service;

import com.mine.study.annotation.ApiServer;
import com.mine.study.annotation.Delete;
import com.mine.study.annotation.Get;
import com.mine.study.annotation.Post;
import com.mine.study.bean.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ApiServer(value = "http://localhost:8081", contextPath = "/user", description = "用户api服务")
public interface UserService {

    @Get(value = "/")
    Flux<User> getAllUser();

    @Get("/{id}")
    Mono<User> getUserById(@PathVariable("id") String id);

    @Delete("/{id}")
    Mono<Void> deleteUserById(@PathVariable("id") String id);

    @Post("/")
    Mono<User> createUser(@RequestBody Mono<User> user);
}
