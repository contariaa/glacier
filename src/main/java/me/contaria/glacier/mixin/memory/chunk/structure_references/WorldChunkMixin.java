package me.contaria.glacier.mixin.memory.chunk.structure_references;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.function.Function;

/**
 * @see ProtoChunkMixin
 */
@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    @Redirect(
            method = "getStructureReferences(Lnet/minecraft/world/gen/feature/StructureFeature;)Lit/unimi/dsi/fastutil/longs/LongSet;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"
            )
    )
    private <STRUCTURE_FEATURE> Object skipCreatingEmptyStructureReferenceSet(Map<STRUCTURE_FEATURE, LongSet> structureReferences, STRUCTURE_FEATURE structure, Function<? super STRUCTURE_FEATURE, ? extends LongSet> function) {
        return structureReferences.getOrDefault(structure, LongSets.EMPTY_SET);
    }

    @ModifyArg(
            method = "addStructureReference",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"
            ),
            index = 1
    )
    private Function<? super StructureFeature<?>, ? extends LongSet> reduceStructureReferenceSetInitialCapacity(Function<? super StructureFeature<?>, ? extends LongSet> function) {
        return structure -> new LongOpenHashSet(4);
    }
}
