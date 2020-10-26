package com.sagereal.provider.bean;

import com.sagereal.provider.dao.ReplicationRepository;
import com.sagereal.provider.pojo.Backlog;
import com.sagereal.provider.pojo.Replication;
import com.sagereal.provider.service.BacklogService;
import com.sagereal.provider.service.IssueService;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

//这里是一些定时任务

@Component
public class ScheduledTask {

    static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    IssueService issueService;
    @Autowired
    BacklogService backlogService;
    @Autowired
    ReplicationRepository replicationRepository;
    @Autowired
    MyMailSender myMailSender;


    @Scheduled(cron = "0 0 8 * * ?")
    public void remind() {
        issueService.sendNotification(null);
        backlogService.check(null);
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void rep() {
        for (Replication r : replicationRepository.findAll()) {
            if (!Boolean.TRUE.equals(r.getFinish())) {
                System.out.println("send email:"+r);
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("git");
                message.setTo(r.getEmail());
                String[] ccAddress = new String[]{"zhangguozhong@sagereal.com", "zhangzhefeng@sagereal.com"};
                //message.setCc(ccAddress);
                message.setBcc("liaowenxing@sagereal.com");
                message.setSubject("您的提交存在拷贝文件，请及时处理");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String text = "您在"+format.format(r.getDate())+"的提交："+r.getNote()+"存在拷贝文件：\n"
                                + r.getList()+"\n";

                text += "处理后请前往关闭：\n" +
                        "http://192.168.3.75:8088/provider/rep";
                message.setText(text);
                myMailSender.send(message);
            }
        }
    }

   // @Scheduled(cron = "30 30 * * * ? ")
    public void lookTable() {
        logger.info("定时任务执行" + new Date().toString());
        try {
            NtlmPasswordAuthentication auth =
                    new NtlmPasswordAuthentication("smb://192.168.3.127", "gsm\\63994", "smn123456");
            SmbFile file =
                    new SmbFile("smb://192.168.3.127/02_sw/127文件目录/04_部门管理/02_小组管理/01_MMI组/00_MMI组代码提交检查记录/", auth);
            access(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void access(SmbFile file) {
        try {
            if (file.isDirectory()) {
                for (SmbFile child : file.listFiles()) {
                    access(child);
                }
            }
            boolean valid = false;
            long modifiedTime = 0;
            while (!valid) {
                long random = Math.abs(new Random().nextLong() % (60 * 60 * 24 * 1000 * 2));
                modifiedTime = new Date().getTime() - random;
                Date date = new Date(modifiedTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                valid = calendar.get(Calendar.HOUR_OF_DAY) > 8 && calendar.get(Calendar.HOUR_OF_DAY) < 17;
            }
            file.setLastModified(modifiedTime);
            logger.info("auto update:" + file.getName() + " time:" + new Date(file.getLastModified()));
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }
}
