package com.ursful.framework.mina.test;

import com.ursful.framework.mina.message.support.Message;

/**
 * <p>项目名称: ursful-mina </p>
 * <p>描述:  </p>
 * <p>创建时间:2020/10/28 10:56 </p>
 * <p>公司信息:厦门海迈科技股份有限公司&gt;研发中心&gt;框架组</p>
 *
 * @author huangyonghua, jlis@qq.com
 */
public class ExtMessage extends Message {

    public int getMsgType(){
        Integer obj = (Integer) this.get("msgType");
        if (obj != null){
            return obj.intValue();
        }
        return -1;
    }

    public void setMsgType(int msgType){
        this.put("msgType", msgType);
    }

    public static void main(String[] args) {
        ExtMessage message = Message.parseMessage(new byte[]{}, ExtMessage.class);
    }

}
