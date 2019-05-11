package com.hxht.dnsftp.inter.deleteftpfileinter;

import com.hxht.dnsftp.model.FileList;

import java.util.List;

public interface IDeleteFtpFile {

    /**
     * 查询符合删除条件的数据
     */
    List<FileList> deleteFtpFileGetData();

    /**
     * 数据的处理
     */
    List<FileList> dataHand(List<FileList> list);


    /**
     * 数据状态的更改
     */
    int deleteFtpFileChaStatus(List<FileList> list);

}
