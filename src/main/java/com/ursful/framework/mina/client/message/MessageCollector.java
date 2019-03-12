package com.ursful.framework.mina.client.message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageCollector {

//    private Map<String, Message> resultQueue;
//    private boolean cancelled = false;
//    private String filter;
//
//    protected MessageCollector(String filterId) {
//        this(130);
//        this.filter = filterId;
//    }
//
//
//    protected MessageCollector() {
//        this(1000);
//    }
//
//    protected MessageCollector(int maxSize) {
//        this.resultQueue = new HashMap<String, Message>();
//    }
//
//    /**
//     * Explicitly cancels the packet collector so that no more results are
//     * queued up. Once a packet collector has been cancelled, it cannot be
//     * re-enabled. Instead, a new packet collector must be created.
//     */
//    public void cancel() {
//        // If the packet collector has already been cancelled, do nothing.
//        if (!cancelled) {
//            cancelled = true;
//            MessageCenter.getDefault().removePacketCollector(this);
//        }
//    }
//
//    public Message nextResult() {
//        return resultQueue.get(filter);
//    }
//
//    public Message nextResult(long timeout) {
//        try {
//            long size = 0;
//            Message obj = null;
//            while(size < timeout &&  obj == null){
//                obj = nextResult();
//                size += 10;
//                Thread.sleep(10);
//            }
//            return obj;
//        }
//        catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void processPacket(Message message) {
//        if (message == null) {
//            return;
//        }
//        if(message.getId().equals(this.filter)) {
//            resultQueue.put(this.filter, message);
//        }
//    }

    private ArrayBlockingQueue<Message> resultQueue;
    private boolean cancelled = false;
    private String filter;

    protected MessageCollector(String filterId) {
        this(130);
        this.filter = filterId;
    }


    protected MessageCollector() {
        this(130);
    }

    protected MessageCollector(int maxSize) {
        this.resultQueue = new ArrayBlockingQueue<Message>(maxSize);
    }

    /**
     * Explicitly cancels the packet collector so that no more results are
     * queued up. Once a packet collector has been cancelled, it cannot be
     * re-enabled. Instead, a new packet collector must be created.
     */
    public void cancel() {
        // If the packet collector has already been cancelled, do nothing.
        if (!cancelled) {
            cancelled = true;
            MessageCenter.getDefault().removePacketCollector(this);
        }
    }


    public Message pollResult() {
    	return resultQueue.poll();
    }

    public Message nextResult() {
        try {
			return resultQueue.take();
		}catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
    }

    public Message nextResult(long timeout) {
    	try {
			return resultQueue.poll(timeout, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
    }

    public void processPacket(Message message) {
        if (message == null) {
            return;
        }
        if(message.getId().equals(this.filter)) {
            while (!resultQueue.offer(message)) {
                resultQueue.poll();
            }
        }
    }
}