package com.ursful.framework.mina.client.mina.coder;


import com.ursful.framework.mina.client.tools.Cryptor;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.BitTools;
import com.ursful.framework.mina.common.tools.CustomEncryption;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;


public class ClientPacketEncoder implements ProtocolEncoder {

    public synchronized void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        Cryptor cryptor = (Cryptor) session.getAttribute(Cryptor.CRYPTOR);
        if (cryptor != null && cryptor.getSend() != null) {
            synchronized (cryptor.getSend()) {
                byte[] input = ((Packet) message).getBytes();
                byte[] unencrypted = new byte[input.length];
                System.arraycopy(input, 0, unencrypted, 0, input.length);
                byte[] ret = new byte[unencrypted.length + 4];
                byte[] header = cryptor.getSend().getPacketHeader(ret.length);
                CustomEncryption.encryptData(unencrypted);
                cryptor.getSend().crypt(unencrypted);
                System.arraycopy(header, 0, ret, 0, 4);
                System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
                IoBuffer out_buffer = IoBuffer.wrap(ret);
                out.write(out_buffer);
            }
        } else {
            synchronized (session){
                byte[] input = ((Packet) message).getBytes();
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