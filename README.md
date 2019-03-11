# ursful-mina
Message <br/>

Usage:<br/>

Server:<br/>

    UrsServer server = new UrsServer("server1", 9090);<br/>
    server.enableTransfer(false);<br/>
    new Thread(server).start();<br/>
<br/>
Client: <br/>
    InterfaceManager.register(new IMessage() {<br/>
        @Override<br/>
        public void message(Message message, MessageSession session) {<br/>
            System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getType() + ">" + message.getData());<br/>
            if (message.getType() == 0) {<br/>
                Message reply = message.reply("self reply.");<br/>
                session.sendMessage(reply);<br/>

                reply.setId(Message.nextID());<br/>
                message.setType(1);<br/>
                message.setData("new test");<br/>
                session.sendMessage(message);<br/>
            } else if (message.getType() == 1) {<br/>
                Message reply = message.reply("next reply");<br/>
                reply.setId(Message.nextID());<br/>
                reply.setType(2);<br/>
                session.sendMessage(reply);<br/>
            } else {<br/>
                System.out.println("type:" + message.getType());<br/>
            }<br/>
        }<br/>
    });<br/>
<br/>
    InterfaceManager.register(new IPresence() {<br/>
        @Override<br/>
        public void presence(String cid, boolean online,  Map<String, Object> data) {<br/>
            System.out.println("one [" + cid + "]:" + (online?"Online":"Offline") + " >>> " + data);<br/>
        }<br/>
    });<br/>
<br/>
    InterfaceManager.register(new IPresenceInfo() {<br/>
        @Override<br/>
        public void presences(Map<String, Map<String, Object>> cids) {<br/>
            System.out.println(cids);<br/>
        }<br/>
    });<br/>

    InterfaceManager.register(new IClientStatus(){<br/>
<br/>
        @Override<br/>
        public void clientReady(String cid) {<br/>
            System.out.println("clientReady:" + cid);<br/>
        }<br/>
<br/>
        @Override<br/>
        public void clientClose(String cid) {<br/>
            System.out.println("clientClose:" + cid);<br/>
        }<br/>
    });<br/>
<br/>
    UrsClient client = new UrsClient("client10", "127.0.0.1", 9090);<br/>
    new Thread(client).run();<br/>
    Thread.sleep(3*1000);<br/>
    Message message = new Message();<br/>
    message.setType(0);<br/>
    message.setFromCid(client.getCid());<br/>
    message.setToCid(client.getCid());<br/>
    message.setData("test");<br/>
    Message reply = client.getMessageSession().getReply(message, 1000);<br/>
    System.out.println("now:" + reply.getData());<br/>
<br/>
    message.setFromCid(client.getCid());<br/>
    message.setToCid("system@" + client.getServerId());<br/>
    message.setData("servers");<br/>
    reply = client.getMessageSession().getReply(message, 1000);<br/>
    System.out.println("servers:" + reply);<br/>
<br/>
    client.close();<br/>
