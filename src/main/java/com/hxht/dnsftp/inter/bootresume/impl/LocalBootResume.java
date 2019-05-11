package com.hxht.dnsftp.inter.bootresume.impl;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.bootresume.IBootResume;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.DateUtils;
import com.hxht.dnsftp.util.HdfsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(prefix = "ftp.files.trans",name = "type",havingValue = "local")
@Component
public class LocalBootResume implements IBootResume {

    @Value("${ftp.pullip}")
    public String pullip;

    @Value("${ftp.local.store.dir}")
    public String localStoreDir;

    @Autowired
    FileListMapper fileListMapper;

    private static final Logger log = LoggerFactory.getLogger(LocalBootResume.class);
    @Override
    public List<FileList> bootResumeGetData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("downflag", FileList.pullingFile);
        map.put("pullip", pullip);
        List<FileList> list = fileListMapper.bootResumeGetData(map);
        return list;
    }

    @Override
    public boolean dataHand(List<FileList> data) {
        if (data != null && data.size() != 0) {
            //删除数据
            for (int i = 0; i < data.size(); i++) {
                FileList temp = data.get(i);
                new File(localStoreDir+File.separator+temp.getFilename()).delete();
            }
            //改变数据的状态
            int num = bootResumeChaStatus(data);
            log.info("LocalBootResume dataHand改变的条数是" + num);
        }
        return true;
    }

    /***
     * 把这些数据的downflag = -2 的状态改变成-3
     */
    public int bootResumeChaStatus(List<FileList> list) {
        return fileListMapper.bootResumeChaStatus(list);
    }
}
