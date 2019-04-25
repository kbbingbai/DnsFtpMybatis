package com.hxht.dnsftp.dao;

import com.hxht.dnsftp.model.Test;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface TestMapper {

    //BootResume
    List<Test> bootResumeGetData(String downflag);
    int bootResumeChaStatus(List list);

    //DeleteFtpFile
    List<Test> deleteFtpFileGetData(Map map);
    int deleteFtpFileChaStatus(List list);


    //FtpToSql
    int ftpToSqlInsertData(List list);
    int ftpToSqlUpdateData(List list);
    List<Test> ftpToSqlGetData(String deleteflag);

    //PullFile
    Test pullFileGetData(Map map);
    int pullFileStartPull(Map map);
    int pullFileEndPullSucc(Test test);
    int pullFileEndPullFail(Test test);

    //PullTimeoutHandler
    Test pullTimeoutHandlerGetData(Map map);
    int pullTimeoutHandlerChaStatus(Map map);

    //UploadFailFileName
    List<Test> uploadFailFileNameGetData(Map map);

}