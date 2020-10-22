package com.ursful.framework.mina.client.mina.packet;

import com.ursful.framework.mina.common.packet.Packet;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PacketWriter {

    private static Logger log = LoggerFactory.getLogger(PacketWriter.class.getName());

    private Thread writerThread;
    private IoSession session;
    private final BlockingQueue<Packet> queue;
    volatile boolean done = false;

    public IoSession getSession() {
        return session;
    }

    public PacketWriter(IoSession session) {
        this.session = session;
        this.queue = new ArrayBlockingQueue<Packet>(500, true);
        init();
    }

    /** 
    * Initializes the writer in order to be used. It is called at the first connection and also 
    * is invoked if the connection is disconnected by an error.
    */ 
    protected void init() {
        done = false;
        writerThread = new Thread() {
            public void run() {
                writePackets(this);
            }
        };
        writerThread.setName("Message Packet Writer");
        writerThread.setDaemon(true);
    }

    /**
     * Sends the specified packet to the server.
     *
     * @param packet the packet to send.
     */
    public void sendPacket(Packet packet) {
        try {
            queue.put(packet);
        }
        catch (InterruptedException ie) {
            return;
        }
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    /**
     * Starts the packet writer thread and opens a connection to the server. The
     * packet writer will continue writing packets until {@link #shutdown} or an
     * error occurs.
     */
    public void startup() {
        writerThread.start();
    }


    /**
     * Shuts down the packet writer. Once this method has been called, no further
     * packets will be written to the server.
     */
    public void shutdown() {
        done = true;
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    /**
     * Returns the next available packet from the queue for writing.
     *
     * @return the next packet for writing.
     */
    private Packet nextPacket() {
        Packet packet = null;
        // Wait until there's a packet or we're done.
        while ((packet = queue.poll()) == null) {
            try {
                synchronized (queue) {
                    queue.wait();
                }
            }
            catch (InterruptedException ie) {
                // Do nothing
            }
        }
        return packet;
    }

    private void writePackets(Thread thisThread) {
            // Open the stream.
            // Write out packets from the queue.
            while ( (writerThread == thisThread)) {
                Packet packet = nextPacket();
                if (packet != null) {
                    session.write(packet);
                }
//                    if (queue.isEmpty()) {
//                        writer.flush();
//                    }
//                }
            }
            // Flush out the rest of the queue. If the queue is extremely large, it's possible
            // we won't have time to entirely flush it before the socket is forced closed
            // by the shutdown process.
            try {
                while (!queue.isEmpty()) {
                    Packet packet = queue.remove();
                    session.write(packet);
                }
//                writer.flush();
            }
            catch (Exception e) {
                log.warn("Error flushing queue during shutdown, ignore and continue");
            }

            // Delete the queue contents (hopefully nothing is left).
            queue.clear();

    }

}
