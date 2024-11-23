package me.contaria.glacier.mixin.memory.chunk.carving_bitmask;

import me.contaria.glacier.optimization.allocation.carving_bitmask.CarvingMask;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.BitSet;
import java.util.function.Function;

/**
 * @see CarvingMask
 */
@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin {

    @ModifyArg(
            method = "getOrCreateCarvingMask",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;",
                    remap = false
            )
    )
    private Function<GenerationStep.Carver, BitSet> useOptimizedCarvingMask(Function<GenerationStep.Carver, BitSet> function) {
        return carver -> new CarvingMask(16);
    }
}
