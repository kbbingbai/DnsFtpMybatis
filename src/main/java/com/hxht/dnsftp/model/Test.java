package com.hxht.dnsftp.model;

import java.util.Date;

public class Test {

    public static String deleteflagSucc = "1";//成功删除的标志
    public static String deleteflagInit = "0";//删除标志的初始值

    public static String pullSucc = "-1";//成功拉取数据的标志
    public static String pullEnable = "0";//可拉取状态（文件大小稳定的状态）
    public static String pullInitState = "-3";//数据库默认值
    public static String pullingFile = "-2";//文件正在被拉取
    public static String pullFailOne = "1";//拉取失败一次
    public static String pullFailTwo = "2";//拉取失败两次
    public static String pullFailThree = "3";//拉取失败三次


    public static String delRemoteFileInterval = "3";//拉取失败三次


    public Test() {

    }

    public Test(String filename, Long filelen) {
        this.filename = filename;
        this.filelen = filelen;
    }

    private String difdays;//

    private String startpulltimeDateFormat;
    private String pullfiletimeoutmin;




    private Integer id;

    private String filename;

    private String downflag;

    private Date createtime;

    private String deleteflag;

    private Date downtime;

    private Long filelen;

    private Date startpulltime;

    private String pulltimeoutcount;

    private String pullip;

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

    public String getDownflag() {
        return downflag;
    }

    public void setDownflag(String downflag) {
        this.downflag = downflag == null ? null : downflag.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(String deleteflag) {
        this.deleteflag = deleteflag == null ? null : deleteflag.trim();
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

    public String getPulltimeoutcount() {
        return pulltimeoutcount;
    }

    public void setPulltimeoutcount(String pulltimeoutcount) {
        this.pulltimeoutcount = pulltimeoutcount == null ? null : pulltimeoutcount.trim();
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

    @Override
    public String toString() {
        return "Test{" +
                "difdays='" + difdays + '\'' +
                ", startpulltimeDateFormat='" + startpulltimeDateFormat + '\'' +
                ", pullfiletimeoutmin='" + pullfiletimeoutmin + '\'' +
                ", id=" + id +
                ", filename='" + filename + '\'' +
                ", downflag='" + downflag + '\'' +
                ", createtime=" + createtime +
                ", deleteflag='" + deleteflag + '\'' +
                ", downtime=" + downtime +
                ", filelen=" + filelen +
                ", startpulltime=" + startpulltime +
                ", pulltimeoutcount='" + pulltimeoutcount + '\'' +
                ", pullip='" + pullip + '\'' +
                '}';
    }
}