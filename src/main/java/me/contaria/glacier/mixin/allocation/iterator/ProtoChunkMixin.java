package me.contaria.glacier.mixin.allocation.iterator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.Map;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin {

    @WrapOperation(
            method = {
                    "getStructureStarts",
                    "getStructureReferences()Ljava/util/Map;",
                    "getBlockEntityTags"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collections;unmodifiableMap(Ljava/util/Map;)Ljava/util/Map;"
            )
    )
    private Map<?, ?> useEmptyMapInstance(Map<?, ?> structureStarts, Operation<Map<?, ?>> original) {
        if (structureStarts.isEmpty()) {
            return Collections.emptyMap();
        }
        return original.call(structureStarts);
    }
}
