package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.deleteftpfileinter.IDeleteFtpFile;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 删除别人服务器上的文件并且这个文件已经拉取成功，二天执行一回 four
 */
@Component
public class DeleteFtpFile {

   @Resource
   IDeleteFtpFile deleteFtpFileImpl;

    private static final Logger log = LoggerFactory.getLogger(DeleteFtpFile.class);

    /**
     * 第天执行一回，每天晚上22:00执行一回,这个执行的时间并没有硬性的规定，只要每天能够执行一回就可以
     * 删除ftp中的文件
     * 测试用的 @Scheduled(cron = "30 0/3 * * * ?")
     * 真实用的 @Scheduled(cron = "0 0 22 * * ?")
     *  配置在文件中则是 @Scheduled(cron = "${deleteFtpFile.schedule.setting}")
     */
    @Scheduled(cron = "${deleteFtpFile.schedule.setting}")
    public void deleteFtpFile() {
        log.info("DeleteFtpFile deleteFtpFile方法执行");

        List<FileList> getDataList = deleteFtpFileImpl.deleteFtpFileGetData();
        List<FileList> handList = deleteFtpFileImpl.dataHand(getDataList);
        deleteFtpFileImpl.deleteFtpFileChaStatus(handList);

    }

}
