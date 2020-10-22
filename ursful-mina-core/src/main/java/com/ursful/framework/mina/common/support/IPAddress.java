package com.ursful.framework.mina.common.support;

import java.io.Serializable;

/**
 * Created by ynice on 2020/10/8.
 */
public class IPAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ip;
    private int port;

    public IPAddress(){}


    public IPAddress(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
