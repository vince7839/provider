package com.sagereal.apollo.controller;

import com.sagereal.apollo.common.RequestData;
import com.sagereal.apollo.common.Response;
import com.sagereal.apollo.service.ApolloService;
import com.sagereal.apollo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApolloController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    ApolloService apolloService;

    @RequestMapping("/rest/data")
    public Response push(@RequestBody RequestData data,String username,String password){

        System.out.println("push:"+username+" "+password+" "+data);
        if (!authenticationService.authenticate(username,password)){
            return Response.fail("Authentication error/unauthorized",true);
        }
        return apolloService.proccess(data);
    }
}
