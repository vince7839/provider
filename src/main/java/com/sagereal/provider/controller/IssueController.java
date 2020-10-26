package com.sagereal.provider.controller;

import com.sagereal.provider.pojo.Issue;
import com.sagereal.provider.pojo.PageVO;
import com.sagereal.provider.pojo.ResponseDTO;
import com.sagereal.provider.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        String support = issue.getEServiceId().startsWith("SPCSS")?"cq":"mtk";
        issue.setSupport(support);

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
    public PageVO page(@PathVariable Integer page, String proposer,String state,String support){
        System.out.println("find issue by page:"+page+" proposer:"+proposer);
        PageVO vo = new PageVO();
        if (page != null){
            Specification<Issue> sp = new Specification<Issue>() {
                List<Predicate> predicates = new ArrayList<>();
                @Override
                public Predicate toPredicate(Root<Issue> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                    if (!StringUtils.isEmpty(proposer)){
                        predicates.add(cb.like(root.get("proposer").as(String.class),"%"+proposer+"%"));
                    }
                    if (!"all".equals(state)){
                        predicates.add(cb.equal(root.get("state").as(String.class),state));
                    }
                    if (!"all".equals(support)){
                        predicates.add(cb.equal(root.get("support").as(String.class),support));
                    }
                    cq.orderBy(cb.desc(root.get("modifyDate")));
                    Predicate[] arr = new Predicate[predicates.size()];
                    cq.where(predicates.toArray(arr));
                    return cq.getRestriction();
                }
            };
            Page<Issue> result = service.findByPage(page,sp);
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
    public void export(HttpServletResponse response, @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate
            ,Boolean justOpen,String support){

        System.out.println("export issure:"+startDate+" justOpen:"+justOpen);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            String filename = startDate == null ? "issue_all.xlsx"
//                    : String.format("issue_after_%s.xlsx",format.format(startDate));

            String filename = String.format("issue_%s.xlsx",support);

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition","attachment;filename="
                    +filename);
            Specification<Issue> sp = new Specification<Issue>() {
                List<Predicate> predicates = new ArrayList<>();
                @Override
                public Predicate toPredicate(Root<Issue> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                    if (startDate != null){
                        Predicate predicate = cb.greaterThan(root.get("submitDate").as(Date.class),startDate);
                        predicates.add(predicate);
                    }

                    if (!"all".equals(support)){
                        Predicate predicate = cb.equal(root.get("support").as(String.class),support);
                        predicates.add(predicate);
                    }

                    if (Boolean.TRUE.equals(justOpen)||true){
                        Predicate predicate = cb.equal(root.get("state").as(String.class),"open");
                        predicates.add(predicate);
                    }
                    cq.orderBy(cb.desc(root.get("modifyDate")));
                    Predicate[] arr = new Predicate[predicates.size()];
                    cq.where(predicates.toArray(arr));
                    return cq.getRestriction();
                }
            };
            service.export(sp,response.getOutputStream());
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

    @RequestMapping("/issue/nothing/{id}")
    public String nothing(@PathVariable Integer id){
        System.out.println("nothing");
        Issue i = service.findByIssueId(id);
        i.setModifyDate(new Date());
        service.save(i);
        return "nothing";
    }

    @RequestMapping("/issue/close/{id}")
    public String close(@PathVariable Integer id){
        System.out.println("close");
        Issue i = service.findByIssueId(id);
        i.setState("close");
        service.save(i);
        return "thanks";
    }
}
