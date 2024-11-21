package me.contaria.glacier.mixin.gen.carver;

import net.minecraft.fluid.Fluid;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.carver.Carver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(Carver.class)
public abstract class CarverMixin {

    @Shadow
    protected Set<Fluid> carvableFluids;

    @Shadow
    protected abstract boolean isOnBoundary(int minX, int maxX, int minZ, int maxZ, int x, int z);

    /**
     * @author contaria
     * @reason Only look up fluid if section is marked as having any fluids
     */
    @Overwrite
    public boolean isRegionUncarvable(Chunk chunk, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ) {
        ChunkSection[] sections = chunk.getSectionArray();
        for (int x = relMinX; x < relMaxX; x++) {
            for (int z = relMinZ; z < relMaxZ; z++) {
                for (int y = minY - 1; y <= maxY + 1; y++) {
                    ChunkSection section = World.isHeightInvalid(y) ? null : sections[y >> 4];
                    if (!ChunkSection.isEmpty(section) && section.hasRandomFluidTicks() && this.carvableFluids.contains(section.getFluidState(x + (mainChunkX * 16) & 15, y & 15, z + (mainChunkZ * 16) & 15).getFluid())) {
                        return true;
                    }

                    if (y != maxY + 1 && !this.isOnBoundary(relMinX, relMaxX, relMinZ, relMaxZ, x, z)) {
                        y = maxY;
                    }
                }
            }
        }
        return false;
    }
}
