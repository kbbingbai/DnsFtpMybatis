package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.DateUtils;
import com.hxht.dnsftp.util.HdfsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统坏了之后 进行系统重新启动时的程序 file
 * 只需处理downflag=-2，其它的 -3(钢存入的数据)  0（可拉取的状态），1（拉取一次失败的程序）2（拉取两次次失败的程序）1（拉取三次失败的程序）不需要处理
 * @Order(value=1)  five
 */
@Component
public class BootResume implements CommandLineRunner {

    @Value("${hdfs.hdfsUrl}")
    private String hdfsUrl;
    @Value("${hdfs.hdfsDir}")
    private String hdfsDir;

    @Autowired
    FileListMapper fileListMapper;

    private static final Logger log = LoggerFactory.getLogger(BootResume.class);

    @Override
    public void run(String... args) throws Exception {
        log.info("BootResume run方法执行");
        //查询那些downflag=-2的数据,把它的数据改成-3
        List<FileList> data = bootResumeGetData();
        if(data!=null&&data.size() != 0){
            //删除数据
            for (int i = 0; i < data.size(); i++) {
                FileList temp = data.get(i);
                String currday = DateUtils.getCurrDay(temp.getCreatetime());
                String yesterday = DateUtils.getYesterday(temp.getCreatetime());
                boolean flag = HdfsClient.deleteFile(hdfsUrl, hdfsDir, currday, temp.getFilename());
                if (!flag) {
                    HdfsClient.deleteFile(hdfsUrl, hdfsDir, yesterday, temp.getFilename());
                }
            }

            //改变数据的状态
            int num = bootResumeChaStatus(data);
            log.info("改变的条数是" + num);
        }

    }

    /**
     * 查询符合条件的数据 downflag = -2
     */
    public List<FileList> bootResumeGetData() {
        List<FileList> list = fileListMapper.bootResumeGetData(FileList.pullingFile);
        return list;
    }
    /***
     * 把这些数据的downflag = -2 的状态改变成-3
     */
    public int bootResumeChaStatus(List<FileList> list) {
      return fileListMapper.bootResumeChaStatus(list);
    }

}
