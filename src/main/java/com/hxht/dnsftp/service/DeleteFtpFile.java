package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Value("${hdfs.hdfsUrl}")
    private String hdfsUrl;

    @Value("${hdfs.hdfsDir}")
    private String hdfsDir;

    @Value("${ftp.deletefile.interval.day}")
    private String intervalDay;
    @Autowired
    public FtpUtil ftpUtil;

    @Value("${ftp.remoteDirectory}")
    public String remoteDirectory;

    @Autowired
    FileListMapper fileListMapper;

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
        log.info("deleteFtpFile，deleteFtpFile方法执行");
        List<FileList> data = deleteFtpFileGetData();

        FTPClient ftpClient = null;

        if (data != null && data.size() > 0) {
            ftpClient = ftpUtil.getFtpClient();
            List<FileList> succDelFile = new ArrayList<FileList>();

            for (int i = 0; i < data.size(); i++) {
                FileList temp = data.get(i);
                try {
                    boolean flag = ftpClient.deleteFile(temp.getFilename());
                    if (flag) {
                        temp.setDeleteflag(FileList.deleteflagSucc);
                        succDelFile.add(temp);
                    }
                } catch (IOException e) {
                    log.error("DeleteFtpFile ftpClient.deleteFile发生异常deleRemoteFile方法，异常e：{}", e);
                    e.printStackTrace();
                } finally {
                    if (i == data.size() - 1) {
                        deleteFtpFileChaStatus(succDelFile);
                        try {
                            if (ftpClient != null) {
                                ftpClient.logout();
                            }
                        } catch (IOException e) {
                            log.error("DeleteFtpFile ftpClient关闭失败，异常e：{}", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 查询符合删除条件的数据
     */
    public List<FileList> deleteFtpFileGetData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("downflag", FileList.pullSucc);
        map.put("intervalDay", intervalDay);
        map.put("deleteflag", FileList.deleteflagInit);
        List<FileList> data = fileListMapper.deleteFtpFileGetData(map);
        return data;
    }

    //修改数据
    public int deleteFtpFileChaStatus(List<FileList> list) {
        int num = 0;
        if (list.size() > 0) {
            num = fileListMapper.deleteFtpFileChaStatus(list);
        }
        return num;
    }

}
