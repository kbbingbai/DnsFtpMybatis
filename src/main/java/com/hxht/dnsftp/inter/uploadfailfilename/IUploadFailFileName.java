package com.hxht.dnsftp.inter.uploadfailfilename;

import com.hxht.dnsftp.model.FileList;

import java.util.List;

public interface IUploadFailFileName {

    /**
     *得到拉取失败的数据
     */
    List<FileList> getData();

    /**
     * 向Gw上传那些拉取失败的文件
     */
    public void uploadFailFileName(List<FileList> list);

}
