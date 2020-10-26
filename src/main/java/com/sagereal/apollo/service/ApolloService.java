package com.sagereal.apollo.service;

import com.sagereal.apollo.common.RequestData;
import com.sagereal.apollo.common.Response;
import com.sagereal.provider.bean.MyMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApolloService {

    //发邮件的类，具体使用可以参照使用的地方
    @Autowired
    MyMailSender mailSender;

    public Response proccess(RequestData data){
        System.out.println("data:"+data);


        //在这里执行你的逻辑，按照处理结果返回不同的响应


        return Response.SUCCESS;
    }
}
