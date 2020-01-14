package com.sagereal.provider.controller;

import com.sagereal.provider.pojo.Issue;
import com.sagereal.provider.pojo.PageVO;
import com.sagereal.provider.pojo.ResponseDTO;
import com.sagereal.provider.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class IssueController {

    @Autowired
    IssueService service;


    @RequestMapping("/push")
    public String push(){
        System.out.println("push");
        return "push";
    }

    @RequestMapping("/issue/add")
    @ResponseBody
    public ResponseDTO submit(Issue issue) {
        System.out.println("add issue:"+issue);
        ResponseDTO dto = new ResponseDTO();
        try {
            service.save(issue);
            dto.setCode(200);
        }catch (Exception e){

        }
        return dto;
    }

    @RequestMapping("/issue/clear")
    @ResponseBody
    public ResponseDTO clear(@RequestParam(required = true) String password){
        ResponseDTO dto = new ResponseDTO();
        if ("3377501".equals(password)){
            service.deleteAll();
            dto.setCode(200);
        }else{
            dto.setCode(401);
        }
        return dto;
    }

    @RequestMapping("/issue/update")
    @ResponseBody
    public ResponseDTO update(Issue issue){
        System.out.println("update issue:"+issue);
        ResponseDTO dto = new ResponseDTO();
        try{
            service.save(issue);
            dto.setCode(200);
        }catch (Exception e){

        }
        return dto;
    }

    @RequestMapping("/issue/page/{page}")
    @ResponseBody
    public PageVO page(@PathVariable Integer page, String proposer){
        System.out.println("find issue by page:"+page+" proposer:"+proposer);
        PageVO vo = new PageVO();
        if (page != null){
            Page<Issue> result = service.findByPage(page,proposer);
            vo.setCount(result.getTotalElements());
            vo.setPage(page);
            vo.setData(result.getContent());
        }
        return vo;
    }

    @RequestMapping("/issue/{id}")
    public ModelAndView issue(@PathVariable Integer id,ModelAndView mv){
        System.out.println("find issue:"+id);
        Issue issue = service.findByIssueId(id);
        System.out.println("issue:"+issue);
        mv.setViewName("issue");
        mv.addObject("issue",issue);
        return mv;
    }

    @RequestMapping("/issue/export")
    public void export(HttpServletResponse response, @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate){
        System.out.println("export issure:"+startDate);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String filename = startDate == null ? "issue_all.xlsx"
                    : String.format("issue_after_%s.xlsx",format.format(startDate));
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition","attachment;filename="
                    +filename);
            service.export(startDate,response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/issue/delete/{issueId}")
    @ResponseBody
    public ResponseDTO delete(@PathVariable Integer issueId){
        ResponseDTO dto = new ResponseDTO();
        if (issueId != null){
            try {
                service.deleteIssue(issueId);
                dto.setCode(200);
            }catch (Exception e){}
        }
        return dto;
    }

    @RequestMapping("/issue/remind")
    @ResponseBody
    public ResponseDTO remind(String note,String password){
        System.out.println("remind");
        ResponseDTO dto = new ResponseDTO();
        if (!"3377501".equals(password)){
            dto.setCode(403);
        }else{
            dto.setCode(200);
            int count = service.sendNotification(note);
            dto.setData(Integer.valueOf(count));
        }
        return dto;
    }
}
