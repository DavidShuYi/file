package com.iambest.file.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 *
 *
 * FileController实现文件的上传和下载功能
 *
 * @author Jack_David
 * @version 1.0.0
 * @Classname FileController
 * @Date 2021/1/18 11:20
 * @Created by Jack_David
 * @since 1.0.0
 */
@RestController
public class FileController {

    @Value("${local.file.upload.path}")
    String localUploadPath;

    @Value("${local.file.download.path}")
    String localDownloadPath;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    /**
     * 文件上传
     * @param file file对象
     * @return 上传的结果提示
     */
    @RequestMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return "file is empty";
            }

            String fileName = file.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            logger.info("上传的文件名称为：{}，文件的后缀为：{}", fileName, suffixName);
            String localPath = this.localUploadPath;
            String fullPath = localPath + fileName;

            File localFile = new File(fullPath);

            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }
            file.transferTo(localFile);

            return "upload success";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "upload faile";
    }

    /**
     * 根据文件名称到指定路径下面下载文件
     * @param fileName 文件的全名
     * @param response httpResponse对象
     */
    @RequestMapping("/download")
    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response){
        String localFile = this.localDownloadPath + fileName;
        File file = new File(localFile);
        if(file.exists()){
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition","attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            }catch (IOException e) {

            }finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
