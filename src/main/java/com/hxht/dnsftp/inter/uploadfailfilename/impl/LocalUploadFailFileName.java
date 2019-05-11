package com.hxht.dnsftp.inter.uploadfailfilename.impl;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.uploadfailfilename.IUploadFailFileName;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.util.DateUtils;
import com.hxht.dnsftp.util.FtpUtil;
import com.hxht.dnsftp.util.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(prefix = "ftp.files.trans",name = "type",havingValue = "local")
@Component
public class LocalUploadFailFileName implements IUploadFailFileName {

    @Autowired
    public FtpUtil ftpUtil;

    @Value("${ftp.remoteDirectory}")
    public String remoteDirectory;

    @Value("${ftp.pullFailFilesDir}")
    public String pullFailFilesDir;

    @Value("${ftp.local.store.dir}")
    public String localStoreDir;


    @Autowired
    FileListMapper fileListMapper;
    private static final Logger log = LoggerFactory.getLogger(LocalUploadFailFileName.class);

    @Override
    public List<FileList> getData() {
        //得到当前的拉取失败的数据
        Map<String,Object> map = new HashMap<String,Object>();
        String lastDate = DateUtils.getLastDate();
        map.put("downflag", FileList.pullFailThree);
        map.put("deleteflag", FileList.deleteflagInit);
        map.put("startpulltime",lastDate);
        return fileListMapper.uploadFailFileNameGetData(map);
    }

    @Override
    public void uploadFailFileName(List<FileList> failFileList) {
        InputStream inputStream = null;
        if (failFileList!=null&&failFileList.size() > 0) {
            FTPClient ftpClient = ftpUtil.getFtpClient();
            boolean isExistFlag = isExist(ftpClient, pullFailFilesDir);
            List<String> list = new ArrayList<String>();
            failFileList.forEach(n->list.add(n.getFilename()));
            if (isExistFlag) {
                try {
                    inputStream = new ByteArrayInputStream(StringUtils.join(list, "\r\n").getBytes("UTF-8"));
                    ftpClient.storeFile(pullFailFilesDir + File.separator + DateUtils.getLastDate(), inputStream);
                } catch (IOException e) {
                    log.error("LocalUploadFailFileName  uploadFailFileName方法 ftpClient.storeFile异常，异常e：{}", e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            log.error("LocalUploadFailFileName  uploadFailFileName方法 关闭流inputStream异常，异常e：{}", e);
                        }
                    }
                    if (ftpClient != null) {
                        try {
                            ftpClient.logout();
                        } catch (IOException e) {
                            log.error("LocalUploadFailFileName ftpClient关闭失败，异常e：{}", e);
                        }
                    }
                }
            }

            //删除HDFS上面的数据
            String lastDate = DateUtils.getLastDate();
            deleteLocalData(failFileList,lastDate);
        }
    }

    //该方法是删除Hdfs上面的文件
    public void deleteLocalData(List<FileList> fileLists,String lastDate){
        if(fileLists!=null&&fileLists.size()>0){
            for (FileList temp:fileLists) {
                new File(localStoreDir+File.separator+temp.getFilename()).delete();
            }
        }
    }

    //判断是否有pullFailFilesDir文件夹，如果有的话就不用创建了
    public boolean isExist(FTPClient ftpClient, String pullFailFilesDir) {
        boolean flag = false;
        try {
            FTPFile[] arr = ftpClient.listFiles(remoteDirectory, new FTPFileFilter() {
                @Override
                public boolean accept(FTPFile file) {
                    if (file.getName().equals(pullFailFilesDir)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            if (arr.length == 0){
                flag = ftpClient.makeDirectory(pullFailFilesDir);
            } else if (arr.length == 1) {
                flag = true;
            }
        } catch (IOException e) {
            log.error("LocalUploadFailFileName 发生的异常isExist方法，异常e：{}", e);
            e.printStackTrace();
        }
        return flag;
    }
}
