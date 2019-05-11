package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.ftptosqlinter.IFtpToSql;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现从服务器(ftp)上读取文件，把服务器上的文件名存在到储到mysql数据库中 One
 */
@Component
public class FtpToSql {

    @Resource
    private IFtpToSql ftpToSqlImpl;


    @Value("${ftp.remoteDirectory}")
    public String remoteDirectory;

    private static final Logger log = LoggerFactory.getLogger(FtpToSql.class);

    /**
     * 每隔2分钟一次
     * 测试用的 @Scheduled(cron = "0 0/2 * * * ?")
     * 配置在文件中则是 @Scheduled(cron = "${deleteFtpFile.schedule.setting}")
     */
    @Scheduled(cron = "${ftpToSql.schedule.setting}")
    public void ftpToSql() {
        log.info("FtpToSql  保存文件名到数据库，ftpToSql方法执行");
        List<FileList> requiredTestData = ftpToSqlImpl.ftpToSqlGetData();
        Map<String, List<FileList>> map = ftpToSqlImpl.pullData(remoteDirectory,requiredTestData, new ArrayList<FileList>(), new ArrayList<FileList>());
        ftpToSqlImpl.insertUpdateData(map);
    }

}
