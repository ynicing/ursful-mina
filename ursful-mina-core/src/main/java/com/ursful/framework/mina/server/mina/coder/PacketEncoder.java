package com.ursful.framework.mina.server.mina.coder;


import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.BitTools;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.CustomEncryption;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.ursful.framework.mina.server.client.Client;
import org.apache.mina.proxy.utils.ByteUtilities;

public class PacketEncoder implements ProtocolEncoder {

    public synchronized void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
    	Client client = (Client) session.getAttribute(Client.CLIENT_KEY);
        if (client != null && client.getSendCrypto() != null) {
            synchronized (client.getSendCrypto()) {
                byte[] input = ((Packet) message).getBytes();
//                System.out.println("Source:" + ByteUtilities.asHex(input));
                byte[] unencrypted = new byte[input.length];
                System.arraycopy(input, 0, unencrypted, 0, input.length);
                byte[] ret = new byte[unencrypted.length + 4];
                byte[] header = client.getSendCrypto().getPacketHeader(ret.length);
//                System.out.println("header:" + ByteUtilities.asHex(header));
                CustomEncryption.encryptData(unencrypted);
//                System.out.println("en:" + ByteUtilities.asHex(unencrypted));
                client.getSendCrypto().crypt(unencrypted);
//                System.out.println("crypt:" + ByteUtilities.asHex(unencrypted));
                System.arraycopy(header, 0, ret, 0, 4);
                System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
                IoBuffer out_buffer = IoBuffer.wrap(ret);
                out.write(out_buffer);
            }
        } else {
            byte[] input = ((Packet) message).getBytes();
            if (input[0] == -1 || input[0] == 255) {
                IoBuffer out_buffer = IoBuffer.wrap(input);
                out.write(out_buffer);
            } else {
                byte[] ret = new byte[input.length + 4];
                byte[] header = BitTools.getInt(ret.length);
                System.arraycopy(header, 0, ret, 0, 4);
                System.arraycopy(input, 0, ret, 4, input.length);
                IoBuffer out_buffer = IoBuffer.wrap(ret);
                out.write(out_buffer);
            }
        }
    }

    public void dispose(IoSession session) throws Exception {
    }
}