package com.sustc.masterrouter.service;

import org.springframework.stereotype.Component;

@Component
public class AllTime {

    public volatile long fileLogicTime;
    public volatile long fileNetTime;
    public volatile int fileCount;
    public volatile int fileNetCount;

    public volatile long startScheTime;
    public volatile int startScheCount;

    public volatile long startLogicTime1;
    public volatile long startLogicTime2;
    public volatile long startNetTime;
    public volatile int startCount;
    public volatile int startNetCount;

    public volatile long stopLogicTime;
    public volatile long stopNetTime;
    public volatile int stopCount;
    public volatile int stopNetCount;

    public volatile long queryLogicTime;
    public volatile long queryNetTime;
    public volatile int queryCount;
    public volatile int queryNetCount;

    public void clear(){
        this.fileNetCount=0;
        this.fileLogicTime=0;
        this.fileNetTime=0;
        this.fileCount=0;
        this.startLogicTime1=0;
        this.startLogicTime2=0;
        this.startNetTime=0;
        this.startCount=0;
        this.startNetCount=0;
        this.stopLogicTime=0;
        this.stopNetTime=0;
        this.stopCount=0;
        this.stopNetCount=0;
        this.queryLogicTime=0;
        this.queryNetTime=0;
        this.queryCount=0;
        this.queryNetCount=0;
        this.startScheTime=0;
        this.startScheCount=0;
    }
}
