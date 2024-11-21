package me.contaria.glacier.mixin.gen.bedrock;

import me.contaria.glacier.optimization.gen.bedrock.GlacierProtoChunk;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(SurfaceChunkGenerator.class)
public abstract class SurfaceChunkGeneratorMixin {
    @Shadow
    @Final
    protected ChunkGeneratorType field_24774;

    @Shadow
    @Final
    private int field_24779;

    /**
     * @author contaria
     * @reason Set BlockStates directly in section and delay heightmap population.
     */
    @Overwrite
    private void buildBedrock(Chunk chunk, Random random) {
        int floorY = this.field_24774.getBedrockFloorY();
        int ceilingY = this.field_24779 - 1 - this.field_24774.getBedrockCeilingY();
        boolean hasCeiling = ceilingY + 4 >= 0 && ceilingY < this.field_24779;
        boolean hasFloor = floorY + 4 >= 0 && floorY < this.field_24779;
        if (hasCeiling || hasFloor) {
            BlockState bedrock = Blocks.BEDROCK.getDefaultState();
            ProtoChunk protoChunk = (ProtoChunk) chunk;
            Heightmap[] heightmaps = null;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int maxY = -1;
                    if (hasCeiling) {
                        for (int i = 0; i < 5; i++) {
                            if (i <= random.nextInt(5)) {
                                int y = ceilingY - i;
                                protoChunk.getSection(y >> 4).setBlockState(x, y & 15, z, bedrock);
                                if (y > maxY) {
                                    maxY = y;
                                }
                            }
                        }
                    }

                    if (hasFloor) {
                        for (int y = 4; y >= 0; y--) {
                            if (y <= random.nextInt(5)) {
                                protoChunk.getSection(0).setBlockState(x, y, z, bedrock);
                                if (y > maxY) {
                                    maxY = y;
                                }
                            }
                        }
                    }

                    if (maxY >= 0) {
                        if (heightmaps == null) {
                            heightmaps = ((GlacierProtoChunk) protoChunk).glacier$initializeHeightmaps();
                        }
                        for (Heightmap heightmap : heightmaps) {
                            heightmap.trackUpdate(x, maxY, z, bedrock);
                        }
                    }
                }
            }
        }
    }
}
