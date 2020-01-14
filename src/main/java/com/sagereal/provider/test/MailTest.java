package com.sagereal.provider.test;

import com.sagereal.provider.ProviderApplication;
import com.sagereal.provider.bean.ScheduledTask;
import com.sagereal.provider.service.IssueService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProviderApplication.class)
@EnableAutoConfiguration
public class MailTest {

    @Autowired
    IssueService service;

    @Test
    public void test(){
        new ScheduledTask().lookTable();
    }
}
