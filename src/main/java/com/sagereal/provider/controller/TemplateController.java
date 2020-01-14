package com.sagereal.provider.controller;

import com.sagereal.provider.pojo.FileInfo;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TemplateController {

    @RequestMapping("/template")
    @ResponseBody
    public List<FileInfo> list(){
        List<FileInfo> files = new ArrayList<>();
        try {
            NtlmPasswordAuthentication auth =
                    new NtlmPasswordAuthentication("smb://192.168.3.127","gsm\\63994","smn123456");
            SmbFile dir =
                    new SmbFile("smb://192.168.3.127/10_Document Database/01 ISO9001：2015/03 软件开发部/04 MMI组/",auth);
            if (dir.exists()){
                for(SmbFile child:dir.listFiles()){
                    if (child.getName().contains("释放工单")){
                        FileInfo info = new FileInfo();
                        info.setName(child.getName());
                        info.setUrl(child.getPath());
                        files.add(info);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    @RequestMapping("/tfile")
    public void download(String url, HttpServletResponse response){
        System.out.println("download url:"+url);
        if (url == null) return;
        if (!url.endsWith("/")){
            url += "/";
        }
        OutputStream out = null;
        SmbFileInputStream in = null;
        try{
            NtlmPasswordAuthentication auth =
                    new NtlmPasswordAuthentication("smb://192.168.3.127", "gsm\\63994", "smn123456");
            SmbFile file =
                    new SmbFile(url, auth);
            if(file.exists()) {
                System.out.println("file exists");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
                out = response.getOutputStream();
                in = new SmbFileInputStream(file);
                byte[] buffer = new byte[1024];
                int read = 0;
                while((read = in.read(buffer)) != -1){
                    out.write(buffer,0,read);
                    out.flush();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (out != null){
                    out.close();
                }
                if (in != null){
                    in.close();
                }
            }catch (Exception e){}
        }
    }
}
