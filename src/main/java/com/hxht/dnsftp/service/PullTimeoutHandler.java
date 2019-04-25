package com.hxht.dnsftp.service;

import com.hxht.dnsftp.config.MyScheduledTask;
import com.hxht.dnsftp.dao.TestMapper;
import com.hxht.dnsftp.model.Test;
import com.hxht.dnsftp.util.DateUtils;
import com.hxht.dnsftp.util.HdfsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * 当PullFile拉取文件时，如果文件长时间拉取不下来，就放弃本次任务。
 * six
 */
@Component
public class PullTimeoutHandler {
    @Value("${ftp.pullfile.timeout}")
    public String pullfiletimeoutmin;

    @Value("${hdfs.hdfsUrl}")
    private String hdfsUrl;

    @Value("${hdfs.hdfsDir}")
    private String hdfsDir;

    @Value("${ftp.pullip}")
    private String pullip;

    @Autowired
    private MyScheduledTask myScheduledTask;

    @Autowired
    TestMapper testMapper;

    private static final Logger log = LoggerFactory.getLogger(PullTimeoutHandler.class);

    @Scheduled(cron = "0 0/10 * * * ?")
    public void pullTimeoutHandler() {
        log.info("PullTimeoutHandler pullTimeoutHandler方法执行");
        Test data = getData();
        if (data != null) {
            //取消本次任务
            myScheduledTask.myCancelTask(PullFile.class);

            //删除hadoop的文件
            String currday = DateUtils.getCurrDay(data.getStartpulltime());
            boolean flag = HdfsClient.deleteFile(hdfsUrl, hdfsDir, currday, data.getFilename());

            //修改该条数据的状态downflag pulltimeoutcount的状态
            //chaStatus(data);
            log.info("PullTimeoutHandler pullTimeoutHandler方法执行结束");
        }
    }

    public Test getData() {
        Test test = null;
        Map<String,String> map = new HashMap<String,String>();
        map.put("downflag",Test.pullingFile);
        map.put("pullip",pullip);
        map.put("pullfiletimeoutmin",pullfiletimeoutmin);
        try {
            test = testMapper.pullTimeoutHandlerGetData(map);
        } catch (Exception e) {
            log.error("PullTimeoutHandler 方法名获取拉取超时文件，者查询失败{}",e);
        }
        return test;
    }

    /***
     * 把这些数据的downflag = -2 的状态改变成-3
     */
    public int chaStatus(Test test) {
        Map<String,String> map = new HashMap<String,String>();
        String downflag = "0";
        String pulltimeoutcount = "0";
        if (test.getPulltimeoutcount().equals("0")) {
            pulltimeoutcount = "1";
        } else {
            pulltimeoutcount = "2";
            downflag = "3";
        }
        map.put("downflag",downflag);
        map.put("pulltimeoutcount",pulltimeoutcount);
        map.put("id",test.getId().toString());

        int num = 0;
        try{
            num = testMapper.pullTimeoutHandlerChaStatus(map);
        }catch (Exception e){
            log.error("PullTimeoutHandler 改变状态失败{}",e);
        }
        return num;
    }

}
