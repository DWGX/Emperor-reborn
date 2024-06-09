package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk
implements Packet<INetHandlerPlayClient> {
    private int[] xPositions;
    private int[] zPositions;
    private S21PacketChunkData.Extracted[] chunksData;
    private boolean isOverworld;

    public S26PacketMapChunkBulk() {
    }

    public S26PacketMapChunkBulk(List<Chunk> chunks) {
        int i = chunks.size();
        this.xPositions = new int[i];
        this.zPositions = new int[i];
        this.chunksData = new S21PacketChunkData.Extracted[i];
        this.isOverworld = !chunks.get((int)0).getWorld().provider.getHasNoSky();
        for (int j2 = 0; j2 < i; ++j2) {
            Chunk chunk = chunks.get(j2);
            S21PacketChunkData.Extracted s21packetchunkdata$extracted = S21PacketChunkData.getExtractedData(chunk, true, this.isOverworld, 65535);
            this.xPositions[j2] = chunk.xPosition;
            this.zPositions[j2] = chunk.zPosition;
            this.chunksData[j2] = s21packetchunkdata$extracted;
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.isOverworld = buf.readBoolean();
        int i = buf.readVarIntFromBuffer();
        this.xPositions = new int[i];
        this.zPositions = new int[i];
        this.chunksData = new S21PacketChunkData.Extracted[i];
        for (int j2 = 0; j2 < i; ++j2) {
            this.xPositions[j2] = buf.readInt();
            this.zPositions[j2] = buf.readInt();
            this.chunksData[j2] = new S21PacketChunkData.Extracted();
            this.chunksData[j2].dataSize = buf.readShort() & 0xFFFF;
            this.chunksData[j2].data = new byte[S21PacketChunkData.func_180737_a(Integer.bitCount(this.chunksData[j2].dataSize), this.isOverworld, true)];
        }
        for (int k2 = 0; k2 < i; ++k2) {
            buf.readBytes(this.chunksData[k2].data);
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBoolean(this.isOverworld);
        buf.writeVarIntToBuffer(this.chunksData.length);
        for (int i = 0; i < this.xPositions.length; ++i) {
            buf.writeInt(this.xPositions[i]);
            buf.writeInt(this.zPositions[i]);
            buf.writeShort((short)(this.chunksData[i].dataSize & 0xFFFF));
        }
        for (int j2 = 0; j2 < this.xPositions.length; ++j2) {
            buf.writeBytes(this.chunksData[j2].data);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleMapChunkBulk(this);
    }

    public int getChunkX(int p_149255_1_) {
        return this.xPositions[p_149255_1_];
    }

    public int getChunkZ(int p_149253_1_) {
        return this.zPositions[p_149253_1_];
    }

    public int getChunkCount() {
        return this.xPositions.length;
    }

    public byte[] getChunkBytes(int p_149256_1_) {
        return this.chunksData[p_149256_1_].data;
    }

    public int getChunkSize(int p_179754_1_) {
        return this.chunksData[p_179754_1_].dataSize;
    }
}

