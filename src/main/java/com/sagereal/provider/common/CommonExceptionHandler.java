package com.sagereal.provider.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class CommonExceptionHandler {
    private Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    public String handle(Exception e){
        e.printStackTrace();
        LOG.error(e.getMessage());
        return "error:"+e.getMessage();
    }
}
