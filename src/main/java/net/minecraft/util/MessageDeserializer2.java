package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.PacketBuffer;

public class MessageDeserializer2
extends ByteToMessageDecoder {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_) throws Exception {
        p_decode_2_.markReaderIndex();
        byte[] abyte = new byte[3];
        for (int i = 0; i < abyte.length; ++i) {
            if (!p_decode_2_.isReadable()) {
                p_decode_2_.resetReaderIndex();
                return;
            }
            abyte[i] = p_decode_2_.readByte();
            if (abyte[i] < 0) continue;
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer((byte[])abyte));
            try {
                int j2 = packetbuffer.readVarIntFromBuffer();
                if (p_decode_2_.readableBytes() >= j2) {
                    p_decode_3_.add(p_decode_2_.readBytes(j2));
                    return;
                }
                p_decode_2_.resetReaderIndex();
            }
            finally {
                packetbuffer.release();
            }
            return;
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }
}

