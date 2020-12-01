package com.ursful.framework.mina.message;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.Session;
import com.ursful.framework.mina.message.client.IMessage;
import com.ursful.framework.mina.message.client.MessageReader;
import com.ursful.framework.mina.message.support.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ynice on 2020/10/11.
 */
public class MessageManager{

    private static MessageSession session;

    private List<IMessage> messages = new ArrayList<IMessage>();

    public List<IMessage> getMessages(){
        return messages;
    }

    public static void registerMessage(IMessage message){
        MessageManager.getManager().register(message);
    }

    public static void deregisterMessage(IMessage message){
        MessageManager.getManager().deregister(message);
    }

    public void register(IMessage message){
        if(!messages.contains(message)){
            messages.add(message);
        }
    }

    public void deregister(IMessage message){
        messages.remove(message);
    }

//    if(isCluster()){
//        clientHandler.register(new ClusterClientMessagesHandler());
//    }else{
//        ClientMessagesHandler messagesHandler = new ClientMessagesHandler();
//        clientHandler.register(messagesHandler);
//        MessageReader messageReader = new MessageReader(messagesHandler);
//        messageReader.startup();
//    }

    private static MessageManager manager = new MessageManager();

    private UrsClient client;

    private MessageManager() {
    }

    public void setClient(UrsClient client){
        this.client = client;
        session = null;
    }

    public UrsClient getClient(){
        return client;
    }

    public static MessageManager getManager(){
        return manager;
    }

    public MessageSession getSession(){
        if (session == null){
            if(client != null && client.getClientHandler() != null && client.getClientHandler().getWriter() != null){
                session = new MessageSession(client.getClientHandler().getWriter());
            }
        }
        return session;
    }

    public void sendMessage(Message message) {
        getSession().sendMessage(message);
    }

    public Message getReply(Message message, long millisecond){
        return getSession().getReply(message, millisecond);
    }

    public <T extends Message> T getExtensionReply(T message, long millisecond){
        return getSession().getExtensionReply(message, millisecond);
    }

    public static void sendServerMessage(Message message) {
        getManager().getSession().sendMessage(message);
    }

    public static Message getServerReply(Message message, long millisecond){
        return getManager().getSession().getReply(message, millisecond);
    }

    public static <T extends Message> T getServerExtensionReply(T message, long millisecond){
        return getManager().getSession().getExtensionReply(message, millisecond);
    }
}
