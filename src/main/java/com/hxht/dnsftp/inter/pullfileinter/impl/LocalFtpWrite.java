package com.hxht.dnsftp.inter.pullfileinter.impl;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.pullfileinter.IFtpWrite;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;


@ConditionalOnProperty(prefix = "ftp.files.trans",name = "type",havingValue = "local")
@Component
public class LocalFtpWrite implements IFtpWrite {

    private FTPClient ftpClient;

    @Value("${ftp.remoteDirectory}")
    public String remoteDirectory;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Value("${ftp.local.store.dir}")
    public String localDirectory;

    @Autowired
    public FtpUtil ftpUtil;

    @Value("${ftp.pullip}")
    public String pullip;

    @Autowired
    FileListMapper fileListMapper;

    private static final Logger log = LoggerFactory.getLogger(HdfsFtpWrite.class);

    @Override
    public boolean write(FileList test) {
        ftpClient = ftpUtil.getFtpClient();
        //把文件传到hadoop里面，并返回是否已经上传成功的标记
        boolean flag = false;
        String filename = test.getFilename();
        File file = new File(filename);
        FTPFile[] ftpFileArr = null;

        FileOutputStream fileOutputStream = null;
        try {
            ftpClient.changeWorkingDirectory(remoteDirectory);
            ftpFileArr = ftpClient.listFiles(new String(filename.getBytes("UTF-8"), "ISO-8859-1"));
        } catch (IOException e) {
            log.error("LocalFtpWrite  发生异常write方法--listFiles错误，异常e：{}", e);
            e.printStackTrace();
        }

        if (ftpFileArr == null || ftpFileArr.length == 0) {//如果这个文件在远程不存在
            test.setDownflag(test.getDownflag() + 1);
        } else {
            File localFile = new File(localDirectory+File.separator+test.getFilename());
            try {
                fileOutputStream = new FileOutputStream(localFile);
                flag = ftpClient.retrieveFile(test.getFilename(),fileOutputStream);
                //下載成功後的操作
                if (flag) {
                    test.setDownflag(FileList.pullSucc);
                    test.setDowntime(new Timestamp(new Date().getTime()));
                } else {//如果下載不成功
                    test.setDownflag(test.getDownflag() + 1);
                }
            } catch (IOException e) {
                log.error("LocalFtpWrite 发生异常uploadHadoop方法，异常e：{}", e);
                test.setDownflag(test.getDownflag() + 1);
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    log.error("LocalFtpWrite fileOutputStream.close()异常e：{}", e);
                }
//                try {
//                    if (ftpClient != null) {
//                        ftpClient.completePendingCommand();
//                    }
//                } catch (IOException e) {
//                    log.error("LocalFtpWrite ftpClient.completePendingCommand()异常e：{}", e);
//                }

                try {
                    if (ftpClient != null) {
                        ftpClient.logout();
                    }
                } catch (IOException e) {
                    log.error("LocalFtpWrite ftpClient.logout();异常e：{}", e);
                }
            }
        }
        return flag;
    }

    @Override
    public FileList getFileName() {
        FileList test = null;
        try {
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("pullEnable", FileList.pullEnable);
            map.put("pullFailOne", FileList.pullFailOne);
            map.put("pullFailTwo", FileList.pullFailTwo);
            test = fileListMapper.pullFileGetData(map);
        } catch (Exception e) {
            log.error("LocalFtpWrite  方法名getFileName失败");
        }
        if (test != null) {
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("downflag", FileList.pullingFile);
            map.put("pullip",pullip);
            map.put("id",test.getId().toString());
            int updateCoun = fileListMapper.pullFileStartPull(map);
            if (updateCoun == 1) {
                log.info("LocalFtpWrite  getFileName得到了一条数据，得到的数据是:" + test);
            } else{
                log.info("LocalFtpWrite  getFileName没有得到数据 ");
                test = null;
            }
        }
        return test;
    }

    @Override
    public void pullFailFile(FileList test) {
        fileListMapper.pullFileEndPullFail(test);
    }

    @Override
    public void pullSuccFile(FileList test) {
        fileListMapper.pullFileEndPullSucc(test);
    }

}
