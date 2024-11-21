package me.contaria.glacier.optimization.chunk.create_future;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkPos;

public class UnloadedChunkHolderAt implements ChunkHolder.Unloaded {
    private final ChunkPos pos;

    public UnloadedChunkHolderAt(int x, int z) {
        this.pos = new ChunkPos(x, z);
    }

    @Override
    public String toString() {
        return "Unloaded " + this.pos;
    }
}
