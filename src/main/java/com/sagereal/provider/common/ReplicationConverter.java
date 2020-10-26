package com.sagereal.provider.common;

import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplicationConverter implements AttributeConverter<List<String>,String> {
    @Override
    public String convertToDatabaseColumn(List<String> list) {
        String value = "";
        if(list != null){
            for (String str:list) {
                value += str + ",";
            }
        }
        return value;
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        List<String> list = new ArrayList<>();
        if(StringUtils.isEmpty(s)){
            return list;
        }
        String[] arr = s.split(",");
        for (String path:arr) {
            if(!StringUtils.isEmpty(path)){
                list.add(path);
            }
        }
        return list;
    }
}
