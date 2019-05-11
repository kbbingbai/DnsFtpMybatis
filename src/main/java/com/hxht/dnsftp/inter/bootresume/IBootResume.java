package com.hxht.dnsftp.inter.bootresume;

import com.hxht.dnsftp.model.FileList;

import java.util.List;

public interface IBootResume {

    /**
     *得到要处理的数据
     * @return
     */
    List<FileList> bootResumeGetData();

    /**
     * 进行数据的处理
     */
    boolean dataHand(List<FileList> list);

}
