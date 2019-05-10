package com.hxht.dnsftp.dao;

import com.hxht.dnsftp.model.FileList;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface FileListMapper {

    //BootResume
    List<FileList> bootResumeGetData(Map map);
    int bootResumeChaStatus(List list);

    //DeleteFtpFile
    List<FileList> deleteFtpFileGetData(Map map);
    int deleteFtpFileChaStatus(List list);

    //FtpToSql
    int ftpToSqlInsertData(List list);
    int ftpToSqlUpdateData(List list);
    List<FileList> ftpToSqlGetData(Integer deleteflag);

    //PullFile
    FileList pullFileGetData(Map map);
    int pullFileStartPull(Map map);
    int pullFileEndPullSucc(FileList test);
    int pullFileEndPullFail(FileList test);

    //PullTimeoutHandler
    FileList pullTimeoutHandlerGetData(Map map);
    int pullTimeoutHandlerChaStatus(Map map);

    //UploadFailFileName
    List<FileList> uploadFailFileNameGetData(Map map);
    List<FileList> uploadFailFileNameDelHdfsData(Map map);


}