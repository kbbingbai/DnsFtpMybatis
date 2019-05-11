package com.hxht.dnsftp.service;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.bootresume.IBootResume;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.DateUtils;
import com.hxht.dnsftp.util.HdfsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统坏了之后 进行系统重新启动时的程序 file
 * 只需处理downflag=-2，其它的 -3(钢存入的数据)  0（可拉取的状态），1（拉取一次失败的程序）2（拉取两次次失败的程序）1（拉取三次失败的程序）不需要处理
 * @Order(value=1)  five
 */
//@Component
public class BootResume implements CommandLineRunner {

    @Resource
    IBootResume bootResumeImpl;

    private static final Logger log = LoggerFactory.getLogger(BootResume.class);

    @Override
    public void run(String... args) throws Exception {
        log.info("BootResume run方法执行");
        //各个ip查询那些downflag=-2的数据,把它的数据改成-3
        List<FileList> data = bootResumeImpl.bootResumeGetData();
        bootResumeImpl.dataHand(data);
    }

}
