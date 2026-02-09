package me.contaria.glacier;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ProtoChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Glacier {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String VERSION = FabricLoader.getInstance().getModContainer("glacier").orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();

    /**
     * Mutable joinBlockPos to reduce BlockPos allocations.
     *
     * @see ProtoChunk#joinBlockPos
     */
    public static BlockPos.Mutable joinBlockPos(BlockPos.Mutable mutable, short sectionRel, int sectionY, ChunkPos chunkPos) {
        return mutable.set((sectionRel & 15) + (chunkPos.x << 4), (sectionRel >>> 4 & 15) + (sectionY << 4), (sectionRel >>> 8 & 15) + (chunkPos.z << 4));
    }
}
