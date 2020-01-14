package com.sagereal.provider.pojo;

import lombok.Data;

@Data
public class ResponseDTO {
    Integer code;
    String msg;
    Object data;
}
