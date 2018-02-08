package com.lw.thewar.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */
public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIP = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static  String ftpPass = PropertiesUtil.getProperty("ftp.pass");



    public FTPUtil(String ip, int port, String user, String pass){
        this.ip = ip;
        this.user = user;
        this.pass = pass;
        this.port = port;
    }

    public static boolean upLoadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIP,21,ftpUser,ftpPass);
        logger.info("开始连接服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("结束上传，上传结果：{}",result);
        return result;
    }

    public static boolean delefile(String fileName) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIP,21,ftpUser,ftpPass);
        logger.info("开始连接服务器");
        boolean result = ftpUtil.deleteFileFtp("img",fileName);
        logger.info("删除结束，结果：{}",result);
        return result;
    }

    private  boolean deleteFileFtp(String remotePath,String fileName) throws IOException {

        boolean dele = true;

            if(connectServer(this.ip,this.user,this.pass,this.port)){
                try {
                    ftpClient.changeWorkingDirectory(remotePath);
                    ftpClient.setBufferSize(1024);
                    ftpClient.setControlEncoding("UTF-8");
                    ftpClient.deleteFile(fileName);
                } catch (IOException e) {
                    dele=false;
                    e.printStackTrace();
                }finally {
                    ftpClient.disconnect();
                }

            }
        return dele;
    }
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean upload = true;
        FileInputStream fis = null;
        if(connectServer(this.ip,this.user,this.pass,this.port)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                    if(fis != null){
                        fis.close();
                    }
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                upload = false;
                e.printStackTrace();
            }finally {
                if(fis != null){
                    fis.close();
                }
                ftpClient.disconnect();

            }
        }
        return upload;
    }

    private boolean connectServer(String ip,String user,String pass,int port){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pass);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    private String ip;
    private String user;
    private String pass;
    private int port;

    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port =port;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
