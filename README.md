# ursful-mina
Message

Usage:
Server:

    UrsServer server = new UrsServer("server1", 9090);
    server.enableTransfer(false);
    new Thread(server).start();

Client:
    InterfaceManager.register(new IMessage() {
        @Override
        public void message(Message message, MessageSession session) {
            System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getType() + ">" + message.getData());
            if (message.getType() == 0) {
                Message reply = message.reply("self reply.");
                session.sendMessage(reply);

                reply.setId(Message.nextID());
                message.setType(1);
                message.setData("new test");
                session.sendMessage(message);
            } else if (message.getType() == 1) {
                Message reply = message.reply("next reply");
                reply.setId(Message.nextID());
                reply.setType(2);
                session.sendMessage(reply);
            } else {
                System.out.println("type:" + message.getType());
            }
        }
    });

    InterfaceManager.register(new IPresence() {
        @Override
        public void presence(String cid, boolean online,  Map<String, Object> data) {
            System.out.println("one [" + cid + "]:" + (online?"Online":"Offline") + " >>> " + data);
        }
    });

    InterfaceManager.register(new IPresenceInfo() {
        @Override
        public void presences(Map<String, Map<String, Object>> cids) {
            System.out.println(cids);
        }
    });

    InterfaceManager.register(new IClientStatus(){

        @Override
        public void clientReady(String cid) {
            System.out.println("clientReady:" + cid);
        }

        @Override
        public void clientClose(String cid) {
            System.out.println("clientClose:" + cid);
        }
    });

    UrsClient client = new UrsClient("client10", "127.0.0.1", 9090);
    new Thread(client).run();
    Thread.sleep(3*1000);
    Message message = new Message();
    message.setType(0);
    message.setFromCid(client.getCid());
    message.setToCid(client.getCid());
    message.setData("test");
    Message reply = client.getMessageSession().getReply(message, 1000);
    System.out.println("now:" + reply.getData());

    message.setFromCid(client.getCid());
    message.setToCid("system@" + client.getServerId());
    message.setData("servers");
    reply = client.getMessageSession().getReply(message, 1000);
    System.out.println("servers:" + reply);

    client.close();
