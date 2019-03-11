package com.ursful.framework.mina.server.mina.coder;


import com.ursful.framework.mina.common.tools.AesOfb;
import com.ursful.framework.mina.common.tools.BitTools;
import com.ursful.framework.mina.common.tools.CustomEncryption;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.ursful.framework.mina.server.client.Client;

public class PacketDecoder extends CumulativeProtocolDecoder {

    private static final String DECODER_STATE_KEY = PacketDecoder.class.getName() + ".STATE";

    private static class DecoderState {
        public int packetlength = -1;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        Client client = (Client) session.getAttribute(Client.CLIENT_KEY);
        DecoderState decoderState = (DecoderState) session.getAttribute(DECODER_STATE_KEY);
        if (decoderState == null) {
            decoderState = new DecoderState();
            session.setAttribute(DECODER_STATE_KEY, decoderState);
        }
        if(client != null && client.getReceiveCrypto() != null){
            synchronized (client.getReceiveCrypto()){
                //判断长度是否大于 4个字节
                if (in.remaining() >= 4 && decoderState.packetlength == -1) {
                    int packetHeader = in.getInt();//获取packet长度，需要解码
                    decoderState.packetlength = AesOfb.getPacketLength(packetHeader) - 4;
                } else if (in.remaining() < 4 && decoderState.packetlength == -1) {
                    return false;//继续解码...没有足够的数据"
                }
                if (in.remaining() >= decoderState.packetlength && decoderState.packetlength > 0) {
                    byte decryptedPacket[] = new byte[decoderState.packetlength];
                    in.get(decryptedPacket, 0, decoderState.packetlength);
                    decoderState.packetlength = -1;
                    client.getReceiveCrypto().crypt(decryptedPacket);
                    CustomEncryption.decryptData(decryptedPacket);
                    out.write(decryptedPacket);
                    return true;
                } else {
                    return false;
                }
            }
        }else{
            //判断长度是否大于 4个字节
            if (in.remaining() >= 4 && decoderState.packetlength == -1) {
                byte[] lengthBytes = new byte[4];
                in.get(lengthBytes, 0, 4);
                int packetHeader = BitTools.getInt(lengthBytes);//获取packet长度，需要解码
                decoderState.packetlength = packetHeader - 4;
            } else if (in.remaining() < 4 && decoderState.packetlength == -1) {
                return false;//继续解码...没有足够的数据"
            }
            if (in.remaining() >= decoderState.packetlength) {
                byte decryptedPacket[] = new byte[decoderState.packetlength];
                in.get(decryptedPacket, 0, decoderState.packetlength);
                decoderState.packetlength = -1;
                out.write(decryptedPacket);
                return true;
            } else {
                return false;
            }

        }

    }
}