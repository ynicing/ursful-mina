package com.ursful.framework.mina.client.mina;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.exception.RefuseException;
import com.ursful.framework.mina.client.message.IPresence;
import com.ursful.framework.mina.client.message.MessageReader;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.client.tools.ClientPacketCreator;
import com.ursful.framework.mina.client.tools.Cryptor;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.ByteArrayPacket;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.IClientStatus;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ThreadUtils;
import com.ursful.framework.mina.server.client.Client;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler extends IoHandlerAdapter{

	private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

	private Map<Integer, ClientPacketHandler> processorHandlers = new HashMap<Integer, ClientPacketHandler>();

	public ClientPacketHandler getHandler(int packetId) {
		return processorHandlers.get(packetId);
	}

	public  void register(ClientPacketHandler handler) {
		processorHandlers.put(handler.opcode(), handler);
	}

	private UrsClient client;

	private String serverId;
	private String cid;

	private PacketWriter writer;

	public PacketWriter getWriter() {
		return writer;
	}

	public void setWriter(PacketWriter writer) {
		this.writer = writer;
	}

	public ClientHandler(){}

	public ClientHandler(UrsClient client){
		this.client = client;
		this.cid = client.getClientId();
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error(client.isCluster() ? "cluster client:" : "client:" + cause.getMessage(), cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		Cryptor cryptor = (Cryptor)session.getAttribute(Cryptor.CRYPTOR);
		ByteReader reader = new ByteReader((byte[])message);
		if(cryptor == null){
			if(255 == reader.readByte()){
				if(Opcode.HELLO.ordinal() == reader.readShort()){
					int version = reader.readShort();
					this.serverId = reader.readString();
					this.client.setServerId(this.serverId);
					logger.info((client.isCluster() ? "cluster server version:" : "server version:") + version + ">" + this.serverId);
					if(reader.available() > 0) {
						byte[] sendIv = reader.readBytes();
						byte[] recvIv = reader.readBytes();
						session.setAttribute(Cryptor.CRYPTOR, new Cryptor(recvIv, sendIv));
					}else{
						session.setAttribute(Cryptor.CRYPTOR, new Cryptor());
					}
					Opcode code = Opcode.INFO;
					Packet packet = ClientPacketCreator.getInfo(code, this.serverId, this.cid, client.isCluster(), client.getMetaData());
					session.write(packet);
					logger.info("sent:" + this.cid + ">" + this.serverId + ">" + client.getMetaData());
					String clientServerId = cid + "@" + serverId;
					session.setAttribute(Client.CLIENT_ID_KEY, clientServerId);
					writer = new PacketWriter(session);
					writer.startup();
					List<IClientStatus> statuses = InterfaceManager.getObjects(IClientStatus.class);
					for(IClientStatus status : statuses){
						ThreadUtils.start(new Runnable() {
							@Override
							public void run() {
								status.clientReady(clientServerId);
							}
						});
					}
				}
			}
		}else{
			int packetId = reader.readShort();
			ClientPacketHandler packetHandler = getHandler(packetId);
			if (packetHandler != null) {
				try {
					packetHandler.handlePacket(reader, writer);
				}catch (RefuseException e){
					client.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}else{
				logger.warn("unhanlder message : " + packetId + ">" + reader);
			}
		}
		
		//
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		String clientServerId = (String)session.getAttribute(Client.CLIENT_ID_KEY);
		logger.warn("Server close:" + clientServerId);
		if(clientServerId == null){
			return;
		}
		List<IPresence> presenceInfos = InterfaceManager.getObjects(IPresence.class);
		for(IPresence presence : presenceInfos){
			Map<String, Object> data = client.getMetaData();
			data.put("ONLINE", false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					presence.presence(clientServerId,false,client.getMetaData());
				}
			}).start();
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}
	
}
