package com.hxht.dnsftp.inter.ftptosqlinter.impl;

import com.hxht.dnsftp.dao.FileListMapper;
import com.hxht.dnsftp.inter.ftptosqlinter.IFtpToSql;
import com.hxht.dnsftp.model.FileList;
import com.hxht.dnsftp.service.FtpToSql;
import com.hxht.dnsftp.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(prefix = "ftp.files.trans",name = "type",havingValue = "local")
@Component
public class LocalFtpToSql implements IFtpToSql {

    @Autowired
    public FtpUtil ftpUtil;

    private FTPClient ftpClient;

    @Autowired
    FileListMapper fileListMapper;

    @Value("${ftp.remoteDirectory}")
    public String remoteDirectory;

    @Value("${ftp.pullFailFilesDir}")
    public String pullFailFilesDir;

    private static final Logger log = LoggerFactory.getLogger(LocalFtpToSql.class);

    @Override
    public List<FileList> ftpToSqlGetData() {
        return fileListMapper.ftpToSqlGetData(FileList.deleteflagSucc);
    }

    @Override
    public Map<String, List<FileList>> pullData(String remoteDirectory,List<FileList> requiredTestData, List<FileList> pullAddList,List<FileList> pullUpList) {
        // 拉取的数据
        FTPFile[] allFile = null;
        try {
            if (this.remoteDirectory.equals(remoteDirectory)) {
                ftpClient = ftpUtil.getFtpClient();
            }
            allFile = ftpClient.listFiles(remoteDirectory);
        } catch (IOException e) {
            log.error("LocalFtpToSql发生异常，pullData方法--listFiles()方法错误，异常e：{}", e);
        }

        if (allFile != null) {
            for (FTPFile ftpFile : allFile) {
                if (ftpFile.isFile()) {// 判断是一个文件
                    try {
                        String tempFileName = new String(remoteDirectory.getBytes("ISO-8859-1"), "UTF-8") + File.separator
                                + new String(ftpFile.getName().getBytes("ISO-8859-1"), "UTF-8");

                        tempFileName = tempFileName.substring(1, tempFileName.length());
                        FileList isExist = isExist(requiredTestData, tempFileName);//该文件存在于数据库就返回这个对象，否则返回null
                        if (isExist == null) {//说明这个文件还没有保存到数据库
                            pullAddList.add(new FileList(tempFileName, ftpFile.getSize()));
                        }else {//说明这个文件已经保存到数据库当中，看这个文件的大小是否有变化，如果没有变化，则认为这个文件已经稳定，可以设置downflag=0,如果文件的大小有变化则把文件最新的大小保存到数据库
                            if(FileList.pullInitState.equals(isExist.getDownflag())){//判断文件是否是初始状态，如果是初始状态才进行处理，不是就不处理
                                if(isSizeCha(ftpFile, isExist.getFilelen())){//说明这个文件稳定  注意：只有downflag=-3的状态才可以变成downflag=0，其它的状态不可以
                                    isExist.setDownflag(FileList.pullEnable);
                                    pullUpList.add(isExist);
                                }else{
                                    isExist.setFilelen(ftpFile.getSize());
                                    pullUpList.add(isExist);
                                }
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        log.error("LocalFtpToSql发生异常，pullData方法--UnsupportedEncodingException，异常e：{}", e);
                    }
                } else {//它是一个文件夹
                    if (!pullFailFilesDir.equals(ftpFile.getName())) {
                        String remotePath = remoteDirectory + File.separator + ftpFile.getName();
                        pullData(remotePath,requiredTestData,pullAddList, pullUpList);
                    }
                }
            }
        }

        if (this.remoteDirectory.equals(remoteDirectory)) {// 完成了一次任务，就让它存储数据库
            Map<String, List<FileList>> map = new HashMap<String, List<FileList>>();
            map.put("pullAddList", pullAddList);
            map.put("pullUpList", pullUpList);
            try {
                if (ftpClient != null) {
                    ftpClient.logout();
                }
            } catch (IOException e) {
                log.error("LocalFtpToSql发生异常，关闭ftpClient失败：{}", e);
            }

            return map;
        } else {
            return null;
        }
    }

    @Override
    public void insertUpdateData(Map<String, List<FileList>> map) {
        List<FileList> pullInsertList = map.get("pullAddList");//向数据库要新添加的数据
        List<FileList> pullUpdateList = map.get("pullUpList");//向数据库要修改的数据
        if (pullInsertList.size() > 0) {//向数据库中插入新数据数据
            System.out.println(fileListMapper.ftpToSqlInsertData(pullInsertList));;
        }
        if (pullUpdateList.size() > 0) {//向数据库中更新数据
            fileListMapper.ftpToSqlUpdateData(pullUpdateList);
        }
    }


    /**
     * 判断该文件是否已经存在于数据库中，如果存在数据库就返回这个对象，如果不存在就返回null
     */
    public FileList isExist(List<FileList> test, String filename) {
        FileList flag = null;
        if(test!=null){
            for (FileList temp : test) {
                if (temp.getFilename().equals(filename)) {
                    flag = temp;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 查看文件的大小是否有变化
     * @param ftpFile 该时刻服务器上的文件的状态
     * @param size    原文件的大小
     * @return
     */
    public boolean isSizeCha(FTPFile ftpFile, long size) {
        return ftpFile.getSize() == size ? true : false;
    }
}
