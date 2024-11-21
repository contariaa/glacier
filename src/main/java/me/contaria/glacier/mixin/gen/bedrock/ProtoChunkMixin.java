package me.contaria.glacier.mixin.gen.bedrock;

import me.contaria.glacier.optimization.gen.bedrock.GlacierProtoChunk;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumSet;
import java.util.Map;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin implements GlacierProtoChunk {
    @Shadow
    @Final
    private Map<Heightmap.Type, Heightmap> heightmaps;

    @Shadow
    private volatile ChunkStatus status;

    @Override
    public Heightmap[] glacier$initializeHeightmaps() {
        EnumSet<Heightmap.Type> expected = this.status.getHeightmapTypes();
        EnumSet<Heightmap.Type> added = null;

        for (Heightmap.Type type : expected) {
            Heightmap heightmap = this.heightmaps.get(type);
            if (heightmap == null) {
                if (added == null) {
                    added = EnumSet.noneOf(Heightmap.Type.class);
                }
                added.add(type);
            }
        }

        if (added != null) {
            Heightmap.populateHeightmaps((Chunk) this, added);
        }

        Heightmap[] heightmaps = new Heightmap[expected.size()];
        int i = 0;
        for (Heightmap.Type type : expected) {
            heightmaps[i++] = this.heightmaps.get(type);
        }
        return heightmaps;
    }
}
