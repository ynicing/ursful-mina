package com.ursful.framework.mina.test;

import com.ursful.framework.mina.message.support.Message;

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
        Message message = new Message();
        message.setFromCid("from@com");
        message.setToCid("to@com");
        message.setType("message");
        message.put("msgType", 13);

        byte[] data = message.getPacket().getBytes();
        ExtMessage extMessage = Message.parse(data, ExtMessage.class);
        System.out.println(extMessage.getMsgType());
    }

}
