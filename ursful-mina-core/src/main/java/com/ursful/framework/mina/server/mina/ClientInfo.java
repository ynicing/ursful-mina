package com.ursful.framework.mina.server.mina;

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
    private Boolean online;
    private Map<String, Object> data;

    private ClientInfo(){}

    public ClientInfo(String cid, Boolean online, Map<String, Object> data){
        this.cid = cid;
        this.online = online;
        this.data = data;
    }

    public boolean isServer(){
        return "SERVER_CLIENT".equalsIgnoreCase((String)data.get("client_type"));
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
