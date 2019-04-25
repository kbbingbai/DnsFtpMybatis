package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.TestMapper;
import com.hxht.dnsftp.model.Test;
import com.hxht.dnsftp.util.DateUtils;
import com.hxht.dnsftp.util.FtpUtil;
import com.hxht.dnsftp.util.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这个程序的目的是：把那些拉取失败的文件：downflag=3 的文件名上传到他们的服务器的一个文件夹当中，且每一天执行一回，且每一天的数据存在一个文件 three
 */
@Component
public class UploadFailFileName {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Value("${ftp.remoteDirectory}")
    public String remoteDirectory;

    @Value("${ftp.pullFailFilesDir}")
    public String pullFailFilesDir;

    @Autowired
    public FtpUtil ftpUtil;

    @Autowired
    TestMapper testMapper;
    private static final Logger log = LoggerFactory.getLogger(UploadFailFileName.class);

    /**
     * 把拉取失败的文件名，上传到别人的服务器的失败文件夹当中，每一天执行一回，每天1点30分执行
     * @Scheduled(cron = "0 30 1 * * ?")
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void uploadFailFileName() {
        log.info("UploadFailFileName  方法名uploadFailFileName上传错误文件到服务器执行");
        String lastDay = DateUtils.getLastDate();
        List<Test> failFileList = getData(lastDay);
        InputStream inputStream = null;
        if (failFileList!=null&&failFileList.size() > 0) {
            FTPClient ftpClient = ftpUtil.getFtpClient();
            boolean isExistFlag = isExist(ftpClient, pullFailFilesDir);
            List<String> list = new ArrayList<String>();
            failFileList.forEach(n->list.add(n.getFilename()));
            if (isExistFlag) {
                try {
                    inputStream = new ByteArrayInputStream(StringUtils.join(list, "\r\n").getBytes("UTF-8"));
                    ftpClient.storeFile(pullFailFilesDir + "/" + lastDay, inputStream);
                } catch (IOException e) {
                    log.error("UploadFailFileName  uploadFailFileName方法 ftpClient.storeFile异常，异常e：{}", e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            log.error("UploadFailFileName  uploadFailFileName方法 关闭流inputStream异常，异常e：{}", e);
                        }
                    }
                    if (ftpClient != null) {
                        try {
                            ftpClient.logout();
                        } catch (IOException e) {
                            log.error("UploadFailFileName ftpClient关闭失败，异常e：{}", e);
                        }
                    }
                }
            }
        }
    }

    //得到前一天的拉取失败的数据
    public List<Test> getData(String currDate) {
        //得到当前月的拉取失败的数据
        Map<String,String> map = new HashMap<String,String>();
        map.put("downflag",Test.pullFailThree);
        map.put("deleteflag",Test.deleteflagInit);
        map.put("createtime",currDate);
        return testMapper.uploadFailFileNameGetData(map);
    }

    //判断是否有pullFailFilesDir文件夹，如果有的话就不用创建了
    public boolean isExist(FTPClient ftpClient, String pullFailFilesDir) {
        boolean flag = false;
        try {
            FTPFile[] arr = ftpClient.listFiles(remoteDirectory, new FTPFileFilter() {
                @Override
                public boolean accept(FTPFile file) {
                    if (file.getName().equals(pullFailFilesDir)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            if (arr.length == 0){
                flag = ftpClient.makeDirectory(pullFailFilesDir);
            } else if (arr.length == 1) {
                flag = true;
            }
        } catch (IOException e) {
            log.error("UploadFailFileName 发生的异常isExist方法，异常e：{}", e);
            e.printStackTrace();
        }
        return flag;
    }
}
