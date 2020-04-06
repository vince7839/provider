package com.sagereal.provider.service;

import com.sagereal.provider.bean.MyMailSender;
import com.sagereal.provider.dao.BacklogRepository;
import com.sagereal.provider.pojo.Backlog;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class BacklogService {

    @Autowired
    MyMailSender myMailSender;

    @Autowired
    BacklogRepository repository;

    public void add(Backlog backlog){
        backlog.setState("open");
        backlog.setCreateTime(new Date());
        repository.save(backlog);
    }

    public void check(String note){
        List<Backlog> list = repository.findByState("open");
        if (list.isEmpty()){
            return;
        }
        for (Backlog b:list) {
            String[] emailArray = b.getHandler().split(",");
            if(emailArray.length <= 0){
                System.out.println("wrong address");
                return;
            }
            for (int i=0;i<emailArray.length;i++)
            {
                emailArray[i] = emailArray[i].trim();
            }
            System.out.println(Arrays.toString(emailArray));
            String[] ccAddress = new String[]{"zhangguozhong@sagereal.com"};
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ZhangGuozhong");
            message.setTo(emailArray);
            message.setCc(ccAddress);
            message.setBcc("liaowenxing@sagereal.com");
            message.setSubject(b.getTitle());
            String text = b.getSummary() + "\n" +
                    "================================\n";
            if (!StringUtils.isEmpty(note)){
                    text += "张工附：\n" + note + "\n";
                    text += "================================\n";
            }
            text += "这是系统发送的待办提醒，如果已经完成请点击下方链接关闭\n" +
                    "http://192.168.3.75:8088/provider/backlog/close/" + b.getId();
            message.setText(text);
            myMailSender.send(message);
        }
    }

    public List<Backlog> all(){
        return repository.findAll();
    }

    public List<Backlog> list(Integer page, Integer limit){
        Pageable pageable = PageRequest.of(page,limit);
        Page<Backlog> result = repository.findAllByOrderByCreateTimeDesc(pageable);
        return result.getContent();
    }

    public long count(){
        return repository.count();
    }

    public void close(Integer id){
        Backlog b = repository.getOne(id);
        b.setState("close");
        repository.save(b);
    }

    public void export(Boolean justOpen, OutputStream out){
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            List<Backlog> list = Boolean.TRUE.equals(justOpen) ? repository.findByState("open") : repository.findAll();
            XSSFSheet sheet = workbook.createSheet();
            String[] columns = new String[]{"标题","摘要","状态","负责人","创建时间"};
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (list != null){
                for (int i=0;i<list.size()+1;i++){
                    XSSFRow row = sheet.createRow(i);
                    XSSFFont font = workbook.createFont();
                    String[] text = null;
                    if (i == 0){
                        text = columns;
                        font.setColor(IndexedColors.BLUE.index);
                    }else {
                        Backlog b = list.get(i - 1);
                        row.setHeightInPoints(30);
                        text = new String[]{b.getTitle(), b.getSummary()
                                , b.getState(), b.getHandler(), format.format(b.getCreateTime())};
                        font.setColor("open".equals(b.getState()) ? IndexedColors.RED.index : IndexedColors.GREEN.index);
                    }
                    for (int j=0;j<text.length;j++){
                        XSSFCell cell = row.createCell(j);
                        XSSFCellStyle style = workbook.createCellStyle();
                        if(i == 0 || j == 2){
                            style.setFont(font);
                        }
                        style.setAlignment(HorizontalAlignment.LEFT);
                        style.setVerticalAlignment(VerticalAlignment.CENTER);
                        style.getFont().setFontHeightInPoints((short)14);
                        cell.setCellValue(text[j]);
                        cell.setCellStyle(style);
                    }
                }
            }
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (workbook != null){
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
