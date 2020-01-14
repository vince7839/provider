package com.sagereal.provider.controller;

import com.sagereal.provider.pojo.PhoneInfo;
import com.sagereal.provider.pojo.ResponseDTO;
import com.sagereal.provider.service.PhoneInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class PhoneInfoController {

    @Autowired
    PhoneInfoService service;

    @Value("${build.timestamp}")
    String version;

    @RequestMapping("/phoneinfo/{imei}")
    @ResponseBody
    public ResponseDTO findByImei(@PathVariable("imei") String imei){
        List<PhoneInfo> list = service.findByImei(imei);
        ResponseDTO dto = new ResponseDTO();
        dto.setData(list);
        dto.setCode(200);
        return dto;
    }

    @RequestMapping("/remove")
    public String remove(String imei){
        System.out.println("delete:"+imei);
        if (imei != null||!imei.isEmpty()){
            service.deleteAllByImei(imei);
        }
        return "redirect:/test";
    }


    @RequestMapping("/phoneinfo/save")
    @ResponseBody
    public ResponseDTO save(PhoneInfo info){
        System.out.println("save phoneinfo:"+info);
        ResponseDTO dto = new ResponseDTO();
        if (info == null){
            dto.setCode(400);
            dto.setMsg("phoneinfo is null");
        }else{
            if (info.getDate() == null) {
                info.setDate(new Date());
            }
            PhoneInfo value = service.savePhoneInfo(info);
            if (value != null){
                dto.setCode(200);
                dto.setData(value);
            }else{
                dto.setCode(500);
                dto.setMsg("failed");
            }
        }
        return dto;
    }

    @RequestMapping("/test")
    public String test(Model model){
        model.addAttribute("count",service.getCount());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       // format.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(version);
        try {
            Date date = format.parse(version);
           // format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            model.addAttribute("timestamp",format.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "test";
    }

    @RequestMapping("/phoneinfo/page/{page}")
    @ResponseBody
    public List<PhoneInfo> page(@PathVariable("page") Integer page){
        System.out.println("page:"+page);
        List<PhoneInfo> list = new ArrayList<>();
        if (page != null){
            Pageable pageable = PageRequest.of(page,10);
            list = service.findByPage(pageable);
        }
        return list;
    }
}