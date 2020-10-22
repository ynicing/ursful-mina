# ursful-mina
Message Center(C-S-S-C): Client to Server - Server to Client Mode <br/>
<br/>
Usage:<br/>
<br/>
<h2>Server: </h2>
<pre>
public class Server {
    public static void main(String[] args) throws Exception{
        UrsServer server = new UrsServer("server1", 9090);
        server.enableCluster();
        server.register(new MessagesHandler());
        server.register(new ClusterClientMessagesHandler());
        new Thread(server).start();
    }
}
</pre>
<br/>
<h2>Client: </h2>
<pre>
         UrsClient client = new UrsClient("client", "127.0.0.1", 9090);
         client.getMetaData().put("force", "true");
         client.register(new ClientMessagesHandler());
         new Thread(client).run();
</pre>