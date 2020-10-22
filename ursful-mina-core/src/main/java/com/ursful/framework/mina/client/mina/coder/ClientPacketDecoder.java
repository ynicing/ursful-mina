package com.ursful.framework.mina.client.mina.coder;


import com.ursful.framework.mina.client.tools.Cryptor;
import com.ursful.framework.mina.common.tools.AesOfb;
import com.ursful.framework.mina.common.tools.BitTools;
import com.ursful.framework.mina.common.tools.CustomEncryption;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;


public class ClientPacketDecoder extends CumulativeProtocolDecoder {

    private static final String DECODER_STATE_KEY = ClientPacketDecoder.class.getName() + ".ClientSTATE";

    private static class DecoderState {
        public int packetlength = -1;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
    	Cryptor cryptor = (Cryptor) session.getAttribute(Cryptor.CRYPTOR);
        DecoderState decoderState = (DecoderState) session.getAttribute(DECODER_STATE_KEY);
        if (decoderState == null) {
            decoderState = new DecoderState();
            session.setAttribute(DECODER_STATE_KEY, decoderState);
        }
        if(cryptor != null){
            if(cryptor.getRecv() != null) {
                synchronized (cryptor.getRecv()) {
                    if (in.remaining() >= 4 && decoderState.packetlength == -1) {
                        int packetHeader = in.getInt();
                        decoderState.packetlength = AesOfb.getPacketLength(packetHeader) - 4;
                    } else if (in.remaining() < 4 && decoderState.packetlength == -1) {
                        //log.trace("解码...没有足够的数据");
                        return false;
                    }
                    if (in.remaining() >= decoderState.packetlength && decoderState.packetlength > 0) {
                        byte decryptedPacket[] = new byte[decoderState.packetlength];
                        in.get(decryptedPacket, 0, decoderState.packetlength);
                        decoderState.packetlength = -1;
                        cryptor.getRecv().crypt(decryptedPacket);
                        CustomEncryption.decryptData(decryptedPacket);
                        out.write(decryptedPacket);
                        return true;
                    } else {
                        return false;
                    }
                }
            }else{
                synchronized (decoderState){
                    if (in.remaining() >= 4 && decoderState.packetlength == -1) {
                        byte [] lengthBytes = new byte[4];
                        in.get(lengthBytes, 0, 4);
                        int packetHeader = BitTools.getInt(lengthBytes);
                        decoderState.packetlength = packetHeader - 4;
                    } else if (in.remaining() < 4 && decoderState.packetlength == -1) {
                        //log.trace("解码...没有足够的数据");
                        return false;
                    }
                    if (in.remaining() >= decoderState.packetlength && decoderState.packetlength > 0) {
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
        }else{
            byte decryptedPacket[] = new byte[in.remaining()];
            in.get(decryptedPacket, 0, decryptedPacket.length);
            out.write(decryptedPacket);
            return true;
        }



    }
}