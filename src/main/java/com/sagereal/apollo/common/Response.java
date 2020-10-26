package com.sagereal.apollo.common;

import lombok.Data;

@Data
public class Response {
    String Status;
    String ErrorMessage;
    String Retryable;

    public final static Response SUCCESS = new Response();

    static {
        SUCCESS.setStatus("Success");
    }

    public static Response fail(String msg,boolean retry){
        Response response = new Response();
        response.setErrorMessage(msg);
        response.setRetryable(retry ? "Yes" : "No");
        return response;
    }
}
