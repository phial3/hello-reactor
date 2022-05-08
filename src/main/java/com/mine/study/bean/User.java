package com.mine.study.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Principal {

    private String id;

    private String name;

    private String password;

    public User(String name) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

}
