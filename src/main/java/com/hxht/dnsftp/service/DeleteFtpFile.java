package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.TestMapper;
import com.hxht.dnsftp.model.Test;
import com.hxht.dnsftp.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
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
//@Component
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
    TestMapper testMapper;

    private static final Logger log = LoggerFactory.getLogger(DeleteFtpFile.class);

    /**
     * 每隔两天执行一回  @Scheduled(cron = "0 0 4 0/2 * ?")
     */
    @Scheduled(cron = "30 0/3 * * * ?")
    public void deleteFtpFile() {
        log.info("deleteFtpFile，deleteFtpFile方法执行");
        List<Test> data = deleteFtpFileGetData();

        FTPClient ftpClient = null;

        if (data!=null&&data.size() > 0) {
            ftpClient = ftpUtil.getFtpClient();
            List<Test> succDelFile = new ArrayList<Test>();

            for (int i = 0; i < data.size(); i++) {
                Test temp = data.get(i);
                log.info("删除的对象是" + temp);
                String filename = temp.getFilename();
                String[] splitArr = filename.split(".");
                String name = filename.substring(0, 9);
                String numStr = String.valueOf(Integer.parseInt(filename.substring(9, filename.indexOf("."))) + 1);
                try {
                    boolean flag = ftpClient.rename(new String(("/" + filename).getBytes("UTF-8"), "ISO-8859-1"), new String(("/" + name + numStr + ".csv").getBytes("UTF-8"), "ISO-8859-1"));
                    if (flag) {
                        temp.setDeleteflag(Test.deleteflagSucc);
                        succDelFile.add(temp);
                    }
                } catch (IOException e) {
                    log.error("DeleteFtpFile ftpClient.deleteFile发生异常deleRemoteFile方法，异常e：{}", e);
                    e.printStackTrace();
                } finally {
                    if (i==data.size()-1) {
                        deleteFtpFileChaStatus(succDelFile);
                        if(ftpClient!=null){
                            try {
                                ftpClient.logout();
                            } catch (IOException e) {
                                log.error("DeleteFtpFile ftpClient关闭失败，异常e：{}", e);
                            }
                        }
                    }
                }
            }

        }

        //部署的时候 需要的代码
//        for (int i = 0; i < data.size(); i++) {
//            Test temp = data.get(i);
//            try{
//                boolean flag = ftpClient.deleteFile(temp.getFilename());
//                if (flag) {
//                    temp.setDeleteflag(Test.deleteflagSucc);
//                    succDelFile.add(temp);
//                }
//            }catch (IOException e) {
//                log.error("DeleteFtpFile ftpClient.deleteFile发生异常deleRemoteFile方法，异常e：{}", e);
//                e.printStackTrace();
//            } finally {
//                if (i==data.size()-1) {
//                    deleteFtpFileChaStatus(succDelFile);
//                    try {
//                        if(ftpClient!=null){
//                            ftpClient.logout();
//                        }
//                    } catch (IOException e) {
//                        log.error("DeleteFtpFile ftpClient关闭失败，异常e：{}", e);
//                    }
//                }
//            }
//        }

    }

    /**
     * 查询符合删除条件的数据
     */
    public List<Test> deleteFtpFileGetData() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("downflag",Test.pullSucc);
        map.put("intervalDay",intervalDay);
        map.put("deleteflag",Test.deleteflagInit);
        List<Test> data = testMapper.deleteFtpFileGetData(map);
        return data;
    }

    //修改数据
    public int deleteFtpFileChaStatus(List<Test> list) {
        int num = 0;
        if(list.size()>0){
            num = testMapper.deleteFtpFileChaStatus(list);
        }
        return num;
    }

}
