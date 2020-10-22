package com.ursful.framework.mina.message.client;

import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageCenter;
import com.ursful.framework.mina.message.support.MessageCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageReader {

    private static Logger log = LoggerFactory.getLogger(MessageReader.class.getName());

    private Thread readerThread;

    volatile boolean done;

    private String connectionID = null;

    private MessageReader() {
        this.init();
        this.startup();
    }

    private BlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(500, true);


    private static MessageReader reader = new MessageReader();

    public void addMessage(Message message){
        try {
            messages.put(message);
        }
        catch (InterruptedException ie) {
            return;
        }
        synchronized (messages) {
            messages.notifyAll();
        }
    }

    public static MessageReader getReader(){
        return reader;
    }

    private Message nextMessage() {
        Message message = null;
        // Wait until there's a packet or we're done.
        while ((message = messages.poll()) == null) {
            try {
                synchronized (messages) {
                    messages.wait();
                }
            }
            catch (InterruptedException ie) {
                // Do nothing
            }
        }
        return message;
    }
    /**
     * Initializes the reader in order to be used. The reader is initialized during the
     * first connection and when reconnecting due to an abruptly disconnection.
     */
    protected void init() {
        done = false;
        connectionID = null;
        readerThread = new Thread() {
            public void run() {
                parsePackets(this);
            }
        };
        readerThread.setName("Message Packet Reader ");
        readerThread.setDaemon(true);


    }

    public void parsePackets(Thread thisThread){
        while (readerThread == thisThread) {
            Message packet = nextMessage();
            processPacket(packet);
        }
    }

    synchronized public void startup() {
        readerThread.start();
    }

    /**
     * Releases the connection ID lock so that the thread that was waiting can resume. The
     * lock will be released when one of the following three conditions is met:<p>
     *
     * 1) An opening stream was sent from a non XMPP 1.0 compliant server
     * 2) Stream features were received from an XMPP 1.0 compliant server that does not support TLS
     * 3) TLS negotiation was successful
     *
     */
    synchronized private void releaseConnectionIDLock() {
        notify();
    }

    private void processPacket(Message message) {
        if (message == null) {
            return;
        }
        // Loop through all collectors and notify the appropriate ones.
        for (MessageCollector collector : MessageCenter.getPacketCollectors()) {
            collector.processPacket(message);
        }
    }

}