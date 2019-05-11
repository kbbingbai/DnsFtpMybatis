package com.hxht.dnsftp.inter.deleteftpfileinter.impl;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.deleteftpfileinter.IDeleteFtpFile;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(prefix = "ftp.files.trans", name = "type", havingValue = "local")
@Component
public class LocalDeleteFtpFile implements IDeleteFtpFile {

    @Autowired
    FileListMapper fileListMapper;

    @Value("${ftp.deletefile.interval.day}")
    private String intervalDay;

    @Autowired
    public FtpUtil ftpUtil;

    @Value("${ftp.files.delete.dir}")
    private String deleteDirectory;


    private static final Logger log = LoggerFactory.getLogger(LocalDeleteFtpFile.class);

    @Override
    public List<FileList> deleteFtpFileGetData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("downflag", FileList.pullSucc);
        map.put("intervalDay", intervalDay);
        map.put("deleteflag", FileList.deleteflagInit);
        map.put("kafkaflag", FileList.kafkaflagSucc);
        List<FileList> data = fileListMapper.deleteLocalFtpFileGetData(map);
        return data;
    }

    @Override
    public List<FileList> dataHand(List<FileList> data) {
        FTPClient ftpClient = null;
        List<FileList> succDelFile = null;

        if (data != null && data.size() > 0) {
            ftpClient = ftpUtil.getFtpClient();
            succDelFile = new ArrayList<FileList>();

            for (int i = 0; i < data.size(); i++) {
                FileList temp = data.get(i);
                try {
                    boolean flag = ftpClient.rename(temp.getFilename(),deleteDirectory+ File.separator+temp.getFilename());
                    if (flag) {
                        temp.setDeleteflag(FileList.deleteflagSucc);
                        succDelFile.add(temp);
                    }
                } catch (IOException e) {
                    log.error("LocalDeleteFtpFile ftpClient.deleteFile发生异常deleRemoteFile方法，异常e：{}", e);
                    e.printStackTrace();
                } finally {
                    if (i == data.size() - 1) {
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
        return succDelFile;
    }

    @Override
    public int deleteFtpFileChaStatus(List<FileList> list) {
        int num = 0;
        if (list != null && list.size() > 0) {
            num = fileListMapper.deleteFtpFileChaStatus(list);
        }
        return num;
    }

}
