package com.sagereal.provider.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import javax.mail.Address;
import javax.mail.SendFailedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MailSender implements Runnable {

    JavaMailSender mailSender;
    SimpleMailMessage mailMessage;
    static Logger logger = LoggerFactory.getLogger(MailSender.class);
    public MailSender(JavaMailSender mailSender,SimpleMailMessage mailMessage) {
        this.mailSender = mailSender;
        this.mailMessage = mailMessage;
        logger.error(Arrays.toString(mailMessage.getTo()));
    }

    @Override
    public void run() {
        int retry = 1;
        boolean success = false;
        while (retry++ < 100 && !success){
            try {
                mailSender.send(mailMessage);
                success = true;
                logger.debug("发送邮件成功");
            }catch (Exception e){
                logger.error(e.getMessage());
                if (e instanceof MailSendException){
                    Exception[] es = ((MailSendException)e).getMessageExceptions();
                    for (Exception cause:es) {
                        if (cause instanceof SendFailedException){
                            logger.error("SendFailedException");
                            List<String> invalidList = new ArrayList();
                            for (Address address:((SendFailedException)cause).getInvalidAddresses()) {
                                invalidList.add(address.toString());
                            }
                            List<String> toList = new ArrayList();
                            for (String email:mailMessage.getTo()) {
                                if (!invalidList.contains(email)){
                                    toList.add(email);
                                }
                            }
                            logger.info("new address:"+toList);
                            success = toList.isEmpty();
                            if (!success){
                                String[] valid = new String[toList.size()];
                                toList.toArray(valid);
                                mailMessage.setTo(valid);
                            }
                        }
                    }
                }
                logger.error(String.format("发送邮件失败，尝试次数%d",retry));
            }
        }
        logger.info(String.format("邮件发送结果:%s",success));
    }
}
