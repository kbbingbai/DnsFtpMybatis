package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.pullfileinter.IFtpWrite;
import com.hxht.dnsftp.model.FileList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 以mysql当中的数据为标准，查询数据库当中的数据，依据数据库当中的数据从服务器上拉取文件到本地服务器,关改变数据库中相应数据的状态标识 two
 */
//@Component
public class PullFile {

    @Value("${ftp.pullip}")
    public String pullip;

    @Resource
    private IFtpWrite ftpWriteImpl;

    @Autowired
    FileListMapper fileListMapper;

    private static final Logger log = LoggerFactory.getLogger(PullFile.class);

    /**
     * 从服务器上拉取文件
     * 测试用的，真实的也是一样 @Scheduled(fixedRate = 120000, initialDelay = 5000)  注意每台initialDelay隔20秒
     */
    @Scheduled(fixedDelayString = "${pullFile.schedule.setting.fixedDelay}", initialDelayString = "${pullFile.schedule.setting.initialDelayFour}")
    public void pullFile() {
        //得到一个未被标记的文件名，并把他标记为正在在拉取
        log.info("PullFile  进行拉取文件，pullFile方法执行");
        FileList test = ftpWriteImpl.getFileName();
        if (test != null) {
            boolean flag = ftpWriteImpl.write(test);
            if(flag){
                ftpWriteImpl.pullSuccFile(test);
            }else{
                ftpWriteImpl.pullFailFile(test);
            }
        }
    }

}
