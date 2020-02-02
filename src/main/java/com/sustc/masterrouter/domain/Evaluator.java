package com.sustc.masterrouter.domain;

import java.util.concurrent.BlockingQueue;

/**
 * Evalutor状态信息
 *
 */

public class Evaluator{

    private String ip;
    private int port;
    private boolean isStarted;
    private EvaInfo evaInfo;

    public Evaluator(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Evaluator) {
            Evaluator eva = (Evaluator) obj;
            return this.ip.equals(eva.ip) && this.port == eva.port;
        }
        return super.equals(obj);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public EvaInfo getEvaInfo() {
        return evaInfo;
    }

    public void setEvaInfo(EvaInfo evaInfo) {
        this.evaInfo = evaInfo;
    }
}
