package com.sagereal.provider.bean;

import com.sagereal.provider.service.IssueService;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Component
public class ScheduledTask {

    static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    IssueService issueService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void remind(){
        issueService.sendNotification(null);
    }

    @Scheduled(cron = "30 30 * * * ? ")
    public void lookTable(){
        logger.info("定时任务执行"+new Date().toString());
        try {
            NtlmPasswordAuthentication auth =
                    new NtlmPasswordAuthentication("smb://192.168.3.127","gsm\\63994","smn123456");
            SmbFile file =
                    new SmbFile("smb://192.168.3.127/02_sw/127文件目录/04_部门管理/02_小组管理/01_MMI组/00_MMI组代码提交检查记录/",auth);
            access(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void access(SmbFile file){
        try {
            if(file.isDirectory()){
                for(SmbFile child:file.listFiles()){
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
            logger.info("auto update:"+file.getName()+" time:"+new Date(file.getLastModified()));
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }
}
