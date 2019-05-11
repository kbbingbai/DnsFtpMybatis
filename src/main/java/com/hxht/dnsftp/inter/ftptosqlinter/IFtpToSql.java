package com.hxht.dnsftp.inter.ftptosqlinter;

import com.hxht.dnsftp.model.FileList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IFtpToSql {

    /**
     *得到数据要处理的数据
     */
    List<FileList> ftpToSqlGetData();

    /**
     * 处理数据
     * @return
     */
    Map<String, List<FileList>> pullData(String remoteDirectory, List<FileList> requiredTestData, List<FileList> pullAddList,List<FileList> pullUpList);

    /**
     * 把数据入库
     * @param map
     */
    void insertUpdateData(Map<String, List<FileList>> map);
}
