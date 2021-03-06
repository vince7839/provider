package com.sagereal.provider.service;

import com.sagereal.provider.bean.MyMailSender;
import com.sagereal.provider.dao.IssueRepository;
import com.sagereal.provider.pojo.Issue;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class IssueService {

    @Autowired
    IssueRepository repository;

    @Autowired
    MyMailSender myMailSender;

    static Logger logger = LoggerFactory.getLogger(IssueService.class);
    public void save(Issue issue){
        if (issue.getIssueId() == null){
            issue.setSubmitDate(new Date());
        }
        issue.setModifyDate(new Date());
        repository.save(issue);
    }

    public Page<Issue> findByPage(int page,Specification<Issue> sp){
        Pageable pageable = PageRequest.of(page,10);
        Page<Issue> result = repository.findAll(sp,pageable);
        return result;
    }

    public long getCount(){
        return repository.count();
    }

    public Issue findByIssueId(Integer id){
        return repository.findById(id).get();
    }


    public void export(Specification<Issue> sp, OutputStream out){
        InputStream in = null;
        XSSFWorkbook workbook = null;
        try {
            List<Issue> issues = repository.findAll(sp);
            in = new ClassPathResource("excel/issue.xlsx").getInputStream();
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (issues != null){
                for (int i=0;i<issues.size();i++){
                    Issue issue = issues.get(i);
                    XSSFRow row = sheet.createRow(i+1);
                    row.setHeightInPoints(30);
                    String expectDate = issue.getExpectDate() != null
                            ? format.format(issue.getExpectDate()) : "";
                    String projectType = getProjectTypeName(issue.getProjectType());
                    String modifyDate = issue.getModifyDate() != null ?
                            format.format(issue.getModifyDate()) : "";
                    String[] values = new String[]{issue.getEServiceId(),projectType
                            ,getPriorityText(issue.getPriority())
                            ,issue.getDescription(),issue.getReason()
                            ,expectDate,issue.getImpact()
                            ,issue.getState(),issue.getProposer()
                            ,modifyDate};
                    XSSFFont font = workbook.createFont();
                    if (issue.getPriority() == 1){
                        font.setColor(IndexedColors.GREEN.index);
                    }else if(issue.getPriority() == 2){
                        font.setColor(IndexedColors.ORANGE.index);
                    }else if(issue.getPriority() == 3){
                        font.setColor(IndexedColors.RED.index);
                    }

                    for (int j=0;j<values.length;j++){
                        XSSFCell cell = row.createCell(j);
                        //从源码可以看到，如果不设置style那么所有cell.getStyle得到的都是同一个style，
                        // 这就会导致所有单元格的格式都一样，且都为最后一次设置的格式
                        XSSFCellStyle style = workbook.createCellStyle();
                        style.setAlignment( j!=3 && j!=4 && j!=6 ? HorizontalAlignment.CENTER
                                : HorizontalAlignment.LEFT);
                        if(j==2){
                            style.setFont(font);
                        }
                        style.setVerticalAlignment(VerticalAlignment.CENTER);
                        style.getFont().setFontHeightInPoints((short)14);
                        cell.setCellValue(values[j]);
                        cell.setCellStyle(style);
                    }
                }
            }
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if ( in != null) {
                    in.close();
                }
                if (workbook != null){
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteIssue(Integer id){
        System.out.println("delete issue:"+id);
        repository.deleteById(id);
    }

    public void deleteAll(){
        System.out.println("delete all");
        repository.deleteAll();
    }

    public static String getProjectTypeName(Integer type){
        String name = "";
        if(type != null){
            if (type == 1){
                name = "Smart";
            }else if(type == 2){
                name = "Feature";
            }else if(type == 3){
                name = "Smart Feature";
            }
        }
        return name;
    }

    public String getPriorityText(Integer priority){
        if (priority == null){
            return "";
        }
        return priority.equals(1) ? "普通": priority.equals(2) ? "较高" : "很高";
    }

    public int sendNotification(String note){
        Calendar c = Calendar.getInstance();
        //如果是管理员主动发送，则查找2天内未更新的issue，如果自动检测，则查找3天未更新的issue
        int tolerance = (note != null ? 2 : 3);
        //tolerance = -1;
        c.add(Calendar.DATE,-tolerance);
        Date limitDate = c.getTime();
        System.out.println("issues remind which before:"+limitDate);


        List<Issue> issues = repository.findByStateAndModifyDateBefore("open",limitDate);
        Set<String> addressBook = new HashSet<>();
        String content = "以下eService加急请求已长时间未更新，请及时更新\n" +
                "如果您没什么新的信息可提供，请点击列表中的链接表示我已知会，3天内将不再收到提醒邮件\n" +
                "如果问题已解决，请前往页面找到您的issue并将状态改为close：http://192.168.3.75:8088/provider/push\n" +
                "如果是外网请将网址IP换为122.227.143.251\n" +
                "===============================================================================\n";
        if (note != null){
            content += String.format("张工留言：%s\n",note);
            content += "===============================================================================\n";

        }
        for (Issue issue:issues){
            String mail = issue.getMail();
            if (mail == null||mail.isEmpty()){
                System.out.println("skip,null mail for issue:"+issue.getIssueId());
                continue;
            }
            System.out.println("add email to list:"+mail);
            addressBook.add(mail);
            String url = "http://192.168.3.75:8088/provider/issue/nothing/"+issue.getIssueId();
            content += String.format("%s (%s)\n",url,issue.getProposer());
        }
        if (addressBook.isEmpty()){
            System.out.println("address empty,return");
            return 0;
        }

        String[] ccAddress = new String[]{"zhangguozhong@sagereal.com","zhangzhefeng@sagereal.com"};
        String[] emailArray = new String[addressBook.size()];
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("eService_Push");
        message.setTo(addressBook.toArray(emailArray));
        message.setCc(ccAddress);
        message.setBcc("liaowenxing@sagereal.com");
        message.setSubject("请更新eService状态");
        message.setText(content);
        logger.debug(Arrays.toString(emailArray));
        myMailSender.send(message);
        return addressBook.size();
    }
}
