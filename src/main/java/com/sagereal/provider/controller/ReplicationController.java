package com.sagereal.provider.controller;

import com.sagereal.provider.bean.ScheduledTask;
import com.sagereal.provider.dao.ReplicationRepository;
import com.sagereal.provider.pojo.Replication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
public class ReplicationController {

    @Autowired
    ReplicationRepository repository;

    private Logger LOG = LoggerFactory.getLogger(ReplicationController.class);
    @RequestMapping("/replication/add")
    @ResponseBody
    public String handle(Replication rep,Long commitDate){
        System.out.println("add rep:"+rep.toString());
        LOG.error("add rep:"+rep.toString());
        if (commitDate != null){
            Date date = new Date(commitDate*1000);
            rep.setDate(date);
        }
        rep.setFinish(false);
        System.out.println("/replication/add:"+ rep+commitDate);
        repository.save(rep);
        return "ok";
    }

    @RequestMapping("/rep")
    public ModelAndView show(ModelAndView mv){
        List<Replication> list = repository.findAll();
        mv.setViewName("replication");
        mv.addObject("list",list);
        return mv;
    }

    @RequestMapping("/rep/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable Integer id){
        repository.deleteById(id);
        return "ok";
    }

    @RequestMapping("/repinfo/{id}")
    public ModelAndView info(@PathVariable Integer id, ModelAndView mv){
        Replication rep = repository.getOne(id);
        System.out.println("repinfo:"+rep);
        mv.setViewName("repinfo");
        mv.addObject("object",rep);
        return mv;
    }

    @RequestMapping("/rep/finish/{id}")
    @ResponseBody
    public String finish(@PathVariable Integer id){
        System.out.println("finish:"+id);
        Replication replication = repository.getOne(id);
        replication.setFinish(true);
        repository.save(replication);
        return "ok";
    }

    @RequestMapping("/rep/check")
    @ResponseBody
    public String check(){
        //task.rep();
        return "ok";
    }
}
