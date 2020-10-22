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
public class MessageManager extends MessageSession {

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
        super(null);
    }

    public void setClient(UrsClient client){
        this.client = client;
    }

    public UrsClient getClient(){
        return client;
    }

    public static MessageManager getManager(){

        if(manager.getWriter() == null && manager.getClient() != null){
           manager.setWriter(manager.getClient().getClientHandler().getWriter());
        }
//        this.manager.setWriter(client.getClientHandler().getWriter());

//        MessageManager.getManager().setClient(client);
        return manager;
    }
}
