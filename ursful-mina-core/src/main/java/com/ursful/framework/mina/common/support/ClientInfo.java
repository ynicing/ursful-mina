package com.ursful.framework.mina.common.support;

import java.io.Serializable;
import java.util.Map;

/**
 * 类名：ClientInfo
 * 创建者：huangyonghua
 * 日期：2019/3/5 16:48
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class ClientInfo implements Serializable{

    private static final long serialVersionUID = 1L;

    private String cid;
    private boolean online;
    private boolean changed = false;
    private boolean server = false;
    private boolean transfer = false;

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    private Map<String, Object> data;

    private ClientInfo(){}

    public ClientInfo(String cid, boolean online, boolean changed, Map<String, Object> data){
        this.cid = cid;
        this.online = online;
        this.changed = changed;
        this.data = data;
    }

    public ClientInfo(String cid, boolean online, Map<String, Object> data){
        this.cid = cid;
        this.online = online;
        this.data = data;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public boolean isServer(){
        return this.server;
    }

    public <T> T get(String key){
        if(data != null){
            return (T)data.get(key);
        }
        return null;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "cid='" + cid + '\'' +
                ", online=" + online +
                ", changed=" + changed +
                ", data=" + data +
                '}';
    }
}
