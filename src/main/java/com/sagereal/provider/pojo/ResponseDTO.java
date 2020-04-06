package com.sagereal.provider.pojo;

import lombok.Data;

@Data
public class ResponseDTO {
    Integer code;
    String msg;
    Object data;

    public static ResponseDTO SUCCESS(Object data){
        ResponseDTO dto = new ResponseDTO();
        dto.code = 200;
        dto.data = data;
        return dto;
    }

    public static ResponseDTO FAIL(String msg){
        ResponseDTO dto = new ResponseDTO();
        dto.code = 500;
        dto.msg = msg;
        return dto;
    }
}
