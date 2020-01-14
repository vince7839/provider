package com.sagereal.provider.controller;

import com.sagereal.provider.pojo.ResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {
    @RequestMapping("/upload")
    @ResponseBody
    public ResponseDTO upload(MultipartFile file){
        ResponseDTO dto = new ResponseDTO();
        if (file == null||file.isEmpty()){
            dto.setCode(400);
            dto.setMsg("file is null");
        }else{
            dto.setCode(200);
            dto.setMsg("file size:"+file.getSize());
            System.out.println("file size:"+file.getSize()+" file name:"+file.getName());
        }
        return dto;
    }
}
