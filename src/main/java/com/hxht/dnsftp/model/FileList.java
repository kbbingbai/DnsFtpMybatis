package com.hxht.dnsftp.model;

import java.util.Date;

public class FileList {

    public static Integer deleteflagSucc = 1;//成功删除的标志
    public static Integer deleteflagInit = 0;//删除标志的初始值

    public static Integer pullSucc = -1;//成功拉取数据的标志
    public static Integer pullEnable = 0;//可拉取状态（文件大小稳定的状态）
    public static Integer pullInitState = -3;//数据库默认值
    public static Integer pullingFile = -2;//文件正在被拉取
    public static Integer pullFailOne = 1;//拉取失败一次
    public static Integer pullFailTwo = 2;//拉取失败两次
    public static Integer pullFailThree = 3;//拉取失败三次

    public static Integer kafkaflagSucc = 1;//拉取失败三次

    public static String delRemoteFileInterval = "3";//拉取失败三次


    public FileList() {

    }

    public FileList(String filename, Long filelen) {
        this.filename = filename;
        this.filelen = filelen;
    }

    private String difdays;//

    private String startpulltimeDateFormat;
    private String pullfiletimeoutmin;




    private Integer id;

    private String filename;

    private Integer downflag;

    private Date createtime;

    private Integer deleteflag;

    private Date downtime;

    private Long filelen;

    private Date startpulltime;

    private Integer pulltimeoutcount;

    private String pullip;

    private String kafkaflag;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename == null ? null : filename.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getDowntime() {
        return downtime;
    }

    public void setDowntime(Date downtime) {
        this.downtime = downtime;
    }

    public Long getFilelen() {
        return filelen;
    }

    public void setFilelen(Long filelen) {
        this.filelen = filelen;
    }

    public Date getStartpulltime() {
        return startpulltime;
    }

    public void setStartpulltime(Date startpulltime) {
        this.startpulltime = startpulltime;
    }

    public Integer getDownflag() {
        return downflag;
    }

    public void setDownflag(Integer downflag) {
        this.downflag = downflag;
    }

    public Integer getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(Integer deleteflag) {
        this.deleteflag = deleteflag;
    }

    public Integer getPulltimeoutcount() {
        return pulltimeoutcount;
    }

    public void setPulltimeoutcount(Integer pulltimeoutcount) {
        this.pulltimeoutcount = pulltimeoutcount;
    }

    public String getPullip() {
        return pullip;
    }

    public void setPullip(String pullip) {
        this.pullip = pullip == null ? null : pullip.trim();
    }

    public String getDifdays() {
        return difdays;
    }

    public void setDifdays(String difdays) {
        this.difdays = difdays;
    }

    public String getStartpulltimeDateFormat() {
        return startpulltimeDateFormat;
    }

    public void setStartpulltimeDateFormat(String startpulltimeDateFormat) {
        this.startpulltimeDateFormat = startpulltimeDateFormat;
    }

    public String getPullfiletimeoutmin() {
        return pullfiletimeoutmin;
    }

    public void setPullfiletimeoutmin(String pullfiletimeoutmin) {
        this.pullfiletimeoutmin = pullfiletimeoutmin;
    }

    public String getKafkaflag() {
        return kafkaflag;
    }

    public void setKafkaflag(String kafkaflag) {
        this.kafkaflag = kafkaflag;
    }

    @Override
    public String toString() {
        return "FileList{" +
                "difdays='" + difdays + '\'' +
                ", startpulltimeDateFormat='" + startpulltimeDateFormat + '\'' +
                ", pullfiletimeoutmin='" + pullfiletimeoutmin + '\'' +
                ", id=" + id +
                ", filename='" + filename + '\'' +
                ", downflag=" + downflag +
                ", createtime=" + createtime +
                ", deleteflag=" + deleteflag +
                ", downtime=" + downtime +
                ", filelen=" + filelen +
                ", startpulltime=" + startpulltime +
                ", pulltimeoutcount=" + pulltimeoutcount +
                ", pullip='" + pullip + '\'' +
                ", kafkaflag='" + kafkaflag + '\'' +
                '}';
    }
}