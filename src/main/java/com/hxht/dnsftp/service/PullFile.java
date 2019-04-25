package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.TestMapper;
import com.hxht.dnsftp.model.Test;
import com.hxht.dnsftp.util.FtpUtil;
import com.hxht.dnsftp.util.HdfsClient;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * 以mysql当中的数据为标准，查询数据库当中的数据，依据数据库当中的数据从服务器上拉取文件到本地服务器,关改变数据库中相应数据的状态标识 two
 */
@Component
public class PullFile {

    @Autowired
    public FtpUtil ftpUtil;

    @Value("${hdfs.hdfsUrl}")
    public String hdfsUrl;

    @Value("${hdfs.hdfsDir}")
    public String hdfsDir;

    @Value("${ftp.pullip}")
    public String pullip;

    @Autowired
    TestMapper testMapper;

    @Value("${ftp.remoteDirectory}")
    public String remoteDirectory;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private FTPClient ftpClient;

    private static final Logger log = LoggerFactory.getLogger(PullFile.class);

    /**
     * 从服务器上拉取文件
     */
    @Scheduled(fixedRate = 120000, initialDelay = 45000)
    public void pullFile() {
        //得到一个未被标记的文件名，并把他标记为正在在拉取
        log.info("PullFile  进行拉取文件，pullFile方法执行");
        Test test = getFileName();
        if (test != null) {
            ftpClient = ftpUtil.getFtpClient();
            uploadHadoop(test);
        }
    }

    //得到一个未被标记的文件名，并把他标记为正在在拉取
    public Test getFileName() {
        Test test = null;
        try {
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("pullEnable",Test.pullEnable);
            map.put("pullFailOne",Test.pullFailOne);
            map.put("pullFailTwo",Test.pullFailTwo);
            test = testMapper.pullFileGetData(map);
        } catch (Exception e) {
            log.error("PullFile  方法名getFileName失败");
        }
        if (test != null) {
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("downflag",Test.pullingFile);
            map.put("pullip",pullip);
            map.put("id",test.getId().toString());
            int updateCoun = testMapper.pullFileStartPull(map);
            if (updateCoun == 1) {
                log.info("PullFile  getFileName得到了一条数据，得到的数据是:" + test);
            } else{
                log.info("PullFile  getFileName没有得到数据 ");
                test = null;
            }
        }

        return test;
    }

    //把文件传到hadoop里面，并返回是否已经上传成功的标记
    public boolean uploadHadoop(Test test) {
        boolean flag = false;
        String filename = test.getFilename();
        File file = new File(filename);
        InputStream is = null;
        FTPFile[] ftpFileArr = null;
        try {
            ftpClient.changeWorkingDirectory(remoteDirectory);
            ftpFileArr = ftpClient.listFiles(new String(filename.getBytes("UTF-8"), "ISO-8859-1"));
        } catch (IOException e) {
            log.error("PullFile  发生异常uploadHadoop方法--listFiles错误，异常e：{}", e);
            e.printStackTrace();
        }

        if (ftpFileArr == null || ftpFileArr.length == 0) {//如果这个文件在远程不存在
            test.setDownflag(String.valueOf(Integer.parseInt(test.getDownflag()) + 1));
            pullFailFile(test);
        } else {
            try {
                is = ftpClient.retrieveFileStream(new String(filename.getBytes("UTF-8"), "ISO-8859-1"));
                String strDate = DateFormatUtils.format(new Date(), DATE_FORMAT);
                flag = HdfsClient.toHdfs(hdfsUrl, is, filename, hdfsDir + strDate);
                //下載成功後的操作
                if (flag) {
                    test.setDownflag(Test.pullSucc);
                    test.setDowntime(new Timestamp(new Date().getTime()));
                    pullSuccFile(test);
                } else {//如果下載不成功
                    test.setDownflag(String.valueOf(Integer.parseInt(test.getDownflag()) + 1));
                    pullFailFile(test);
                }
            } catch (IOException e) {
                log.error("PullFile   发生异常uploadHadoop方法，异常e：{}", e);
                test.setDownflag(String.valueOf(Integer.parseInt(test.getDownflag()) + 1));
                pullFailFile(test);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    log.error("PullFile   关闭is异常e：{}", e);
                }
                try {
                    if (ftpClient != null) {
                        ftpClient.completePendingCommand();
                    }
                } catch (IOException e) {
                    log.error("PullFile   ftpClient.completePendingCommand()异常e：{}", e);
                }
                try {
                    if (ftpClient != null) {
                        ftpClient.logout();
                    }
                } catch (IOException e) {
                    log.error("PullFile   ftpClient.logout();异常e：{}", e);
                }
            }
        }
        return flag;
    }

    /**
     * 记录拉取失败的数据，传入的List数据
     */
    public void pullFailFile(final Test test) {
        testMapper.pullFileEndPullFail(test);
    }

    /**
     * 记录拉取成功的数据，传入的List数据
     */
    public void pullSuccFile(final Test test) {
        testMapper.pullFileEndPullSucc(test);
    }

}
