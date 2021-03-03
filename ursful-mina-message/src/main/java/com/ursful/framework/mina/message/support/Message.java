package com.ursful.framework.mina.message.support;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名：Message
 * 创建者：huangyonghua
 * 日期：2019/3/4 17:43
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Message implements Serializable{

    private static final long serialVersionUID = 1L;

    public static <T extends Message> T wrapMessage(Message message, Class<T> clazz){
        T t = null;
        if(clazz != null){
            try {
                t = clazz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        t.setData(message.getData());
        t.setFromCid(message.getFromCid());
        t.setToCid(message.getToCid());
        t.setType(message.getType());
        t.setId(message.getId());
        return t;
    }

    public static Message parseMessage(ByteReader byteReader, Class<? extends Message> clazz){
        if(byteReader.position() == 0){
            byteReader.skip(2);
        }
        String id2 = byteReader.readString();
        String type2 = byteReader.readString();
        String fromCid2 = byteReader.readString();
        String toCid2 = byteReader.readString();
        Map<String, Object> data2 = null;
        int available = byteReader.available();
        if(available > 0){
            data2 = byteReader.readObject();
        }
        Message message = null;
        if(clazz != null){
            try {
                message = clazz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }else{
            message = new Message();
        }
        message.setData(data2);
        message.setFromCid(fromCid2);
        message.setToCid(toCid2);
        message.setType(type2);
        message.setId(id2);
        return message;
    }

    public static Message parseMessage(byte [] data){
        return parse(data, null);
    }

    public static Message parseMessage(ByteReader reader){
        return parseMessage(reader, null);
    }

    public static <T extends Message>  T parse(byte [] data, Class<T> clazz){
        ByteReader byteReader = new ByteReader(data);
        byteReader.skip(2);
        return (T)parseMessage(byteReader, clazz);
    }

    private static long cid = 0;

    private static String prefix = StringUtils.randomString(5) + "-";

    public static synchronized String nextID() {
        return prefix + Long.toString(cid++);
    }

    private String id;
    public String getId() {
        if(id == null){
            id = nextID();
        }
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    private String fromCid;
    private String toCid;
    private Map<String, Object> data;

    public static final String BROADCAST = "broadcast";
    public static final String CLIENTS = "clients";
    public static final String CHAT = "chat";//group chat,..
    //类型 0 chat 1 group chat 2 all 3 cluster 4 system
    /*
    //message
    broadcast 广播所有人
    normal：类似于email，主要特点是不要求响应；
    chat：类似于qq里的好友即时聊天，主要特点是实时通讯；
    groupchat：类似于聊天室里的群聊；
    headline：用于发送alert和notification；
    error：如果发送message出错，发现错误的实体会用这个类别来通知发送者出错了


    //presence
    subscribe：订阅其他用户的状态
    probe：请求获取其他用户的状态
    unavailable：不可用，离线（offline）状态

    chat：聊天中
    away：暂时离开
    xa：eXtend Away，长时间离开
    dnd：勿打扰

    //iq
    Get :获取当前域值。类似于http get方法。
    Set :设置或替换get查询的值。类似于http put方法。
    Result :说明成功的响应了先前的查询。类似于http状态码200。
    Error: 查询和响应中出现的错误
     */

    private String type;

    public Message(){
        this.id = nextID();
    }

    public String getFromCid() {
        return fromCid;
    }

    public void setFromCid(String fromCid) {
        this.fromCid = fromCid;
    }

    public String getToCid() {
        return toCid;
    }

    public void setToCid(String toCid) {
        this.toCid = toCid;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Message reply(){
        return reply(new HashMap<String, Object>(), this.toCid);
    }

    public Message reply(Map<String, Object> data){
        return reply(data, this.toCid);
    }

    public boolean isReply(){
        return this.id != null && this.id.startsWith("reply-");
    }

    public Message reply(Map<String, Object> data, String fromCid){
        Message message = new Message();
        message.setId("reply-" + this.id);
        message.setFromCid(fromCid);
        message.setToCid(this.fromCid);
        message.setType(this.type);
        if(data != null) {
            message.setData(data);
        }
        return message;
    }

    public Message transfer(String from, String to){
        Message message = new Message();
        message.setFromCid(from);
        message.setToCid(to);
        message.setType(this.type);
        Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(this.data);
        message.setData(map);
        return message;
    }

    public void putAll(Map<String, Object> value){
        if (this.data == null){
            this.data = new HashMap<String, Object>();
        }
        if(value != null) {
            this.data.putAll(value);
        }
    }


    public void put(String key, Object value){
        if (this.data == null){
            this.data = new HashMap<String, Object>();
        }
        this.data.put(key, value);
    }

    public <T> T get(String key){
        if (this.data != null){
            return (T)this.data.get(key);
        }
        return null;
    }

    public User  getFromUser(){
        return new User(getFromCid());
    }

    public User  getToUser(){
        return new User(getToCid());
    }

    public Packet getPacket(){
        return MessageCreator.createMessage(this);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", from='" + fromCid + '\'' +
                ", to='" + toCid + '\'' +
                ", data=" + (data != null?data.toString():data) +
                '}';
    }
}
