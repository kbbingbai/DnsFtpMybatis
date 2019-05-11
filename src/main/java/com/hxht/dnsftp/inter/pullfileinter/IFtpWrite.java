package com.hxht.dnsftp.inter.pullfileinter;


import com.hxht.dnsftp.model.FileList;

/**
 * 该接口主要作用：
 *      1：把GW上面的文件是拉取到本地的Linux的目录中还是放在HDFS上面或者其它的存储类型的目录中
 */
public interface IFtpWrite {

    //传入一个可以拉取的文件
    boolean write(FileList fileList);

    //得到文件名
    FileList getFileName();

    //摘取失败进行的处理
    void pullFailFile(final FileList test);

    //拉取成功进行的处理
    void pullSuccFile(final FileList test);
}
