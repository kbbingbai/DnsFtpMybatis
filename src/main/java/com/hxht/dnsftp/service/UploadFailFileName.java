package com.hxht.dnsftp.service;

import com.hxht.dnsftp.inter.uploadfailfilename.IUploadFailFileName;
import com.hxht.dnsftp.model.FileList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.List;

/**
 * 这个程序的目的是：把那些拉取失败的文件：downflag=3 的文件名上传到他们的服务器的一个文件夹当中，且每一天执行一回，且每一天的数据存在一个文件 three
 */
//@Component
public class UploadFailFileName {

    @Resource
    private IUploadFailFileName uploadFailFileNameImpl;

    private static final Logger log = LoggerFactory.getLogger(UploadFailFileName.class);

    /**
     * 把拉取失败的文件名，上传到别人的服务器的失败文件夹当中，每一天执行一回，每天0点10分执行
     * 真实的定时是：@Scheduled(cron = "0 10 0 * * ?")
     * 测试的是：@Scheduled(cron = "0 0/10 * * * ?")
     */
    @Scheduled(cron = "${uploadFailFileName.schedule.setting}")
    public void uploadFailFileName() {
        log.info("UploadFailFileName  方法名uploadFailFileName上传错误文件到服务器执行");
        List<FileList> failFileList = uploadFailFileNameImpl.getData();
        uploadFailFileNameImpl.uploadFailFileName(failFileList);
    }
}
