package com.ursful.framework.mina.client.message;

import com.ursful.framework.mina.client.mina.handler.ClientMessagesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MessageReader {

    private static Logger log = LoggerFactory.getLogger(MessageReader.class.getName());

    private Thread readerThread;

    volatile boolean done;

    private ClientMessagesHandler handler;

    private String connectionID = null;

    public MessageReader(ClientMessagesHandler handler) {
        this.handler = handler;
        this.init();
    }


    private Message nextMessage() {
        Message message = null;
        // Wait until there's a packet or we're done.
        while ((message = handler.getMessages().poll()) == null) {
            try {
                synchronized (handler.getMessages()) {
                    handler.getMessages().wait();
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
            System.out.println(System.currentTimeMillis()  + "<<<<message<<<<<" + packet);
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