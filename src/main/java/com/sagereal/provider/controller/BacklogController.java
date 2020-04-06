package com.sagereal.provider.controller;

import com.sagereal.provider.pojo.Backlog;
import com.sagereal.provider.pojo.PageVO;
import com.sagereal.provider.pojo.ResponseDTO;
import com.sagereal.provider.service.BacklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class BacklogController {

    @Autowired
    BacklogService backlogService;

    @RequestMapping("/backlog")
    public String mail(){
        return "notify";
    }

    @RequestMapping("/backlog/check")
    @ResponseBody
    public ResponseDTO check(String note){
        try {
            backlogService.check(note);
            return ResponseDTO.SUCCESS("");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseDTO.FAIL(e.getMessage());
        }
    }

    @RequestMapping("/backlog/init")
    @ResponseBody
    public void init(){
        for (int i=0;i<50;i++){
            Backlog b = new Backlog();
            b.setTitle(""+i);
            b.setHandler("aaaaa");
            b.setSummary("tttt");
            backlogService.add(b);
        }
    }

    @RequestMapping("/backlog/add")
    @ResponseBody
    public ResponseDTO add(Backlog backlog){
        System.out.println("add backlog:"+backlog);
        try {
            backlogService.add(backlog);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseDTO.FAIL("添加失败！");
        }
        return ResponseDTO.SUCCESS("添加成功！");
    }

    @RequestMapping("/backlog/list")
    @ResponseBody
    public PageVO list(Integer page,Integer limit){
        page = page - 1;
        System.out.println("page:"+page+" limit:"+limit);
        List<Backlog> list = backlogService.list(page, limit);
        PageVO vo = new PageVO();
        vo.setData(list);
        vo.setCount(backlogService.count());
        return vo;
    }

    @RequestMapping("/backlog/close/{id}")
    public String close(@PathVariable Integer id){
        backlogService.close(id);
        return "thanks";
    }

    @ResponseBody
    @RequestMapping("/backlog/all")
    public PageVO all(){
        System.out.println("all");
        List<Backlog> list = backlogService.all();
        for (int i=0;i<50;i++){
            Backlog backlog = new Backlog();
            backlog.setTitle(""+i);
            backlog.setHandler(""+i);
            backlog.setCreateTime(new Date());
            list.add(backlog);
        }
        PageVO vo = new PageVO();
        vo.setCount(list.size()+0L);
        vo.setData(list);
        return vo;
    }

    @RequestMapping("/backlog/export")
    public void export(HttpServletResponse response, Boolean justOpen){
        System.out.println("justOpen:"+justOpen);
        try {
            String filename = "backlog.xlsx";
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition","attachment;filename="
                    +filename);
            backlogService.export(justOpen,response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
