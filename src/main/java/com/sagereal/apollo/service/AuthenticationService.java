package com.sagereal.apollo.service;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    final String USERNAME = "admin";
    final String PASSWORD = "admin";

    public boolean authenticate(String username,String password){
       return USERNAME.equals(username) && PASSWORD.equals(password);
    }
}
