package me.contaria.glacier.mixin.memory.chunk.chunk_sections;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

/**
 * Minecraft's chunks are built to lazily initialize ChunkSections, as seen in {@link WorldChunk#setBlockState}.
 * However, during surface generation all sections are initialized through {@link ProtoChunk#getSection}.
 * This optimization only initializes sections if they're actually written to.
 */
@Mixin(value = SurfaceChunkGenerator.class, priority = 1500)
public abstract class SurfaceChunkGeneratorMixin {

    @Redirect(
            method = "populateNoise",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/ProtoChunk;getSection(I)Lnet/minecraft/world/chunk/ChunkSection;"
            )
    )
    private ChunkSection getSectionLazily(ProtoChunk protoChunk, int y, @Share("sectionY") LocalIntRef sectionY) {
        sectionY.set(y);
        return protoChunk.getSectionArray()[y];
    }

    @Redirect(
            method = "populateNoise",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/ChunkSection;getYOffset()I"
            )
    )
    private int getYOffset(ChunkSection section, @Share("sectionY") LocalIntRef sectionY) {
        return sectionY.get() << 4;
    }

    @WrapWithCondition(
            method = "populateNoise",
            at = {
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/chunk/ChunkSection;lock()V"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/chunk/ChunkSection;unlock()V"
                    )
            }
    )
    private boolean noLockingWhenSectionIsNull(ChunkSection section) {
        return section != null;
    }

    @ModifyVariable(
            method = "populateNoise",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getLuminance()I"
            )
    )
    private ChunkSection createSection(ChunkSection section, @Local ProtoChunk protoChunk, @Share("sectionY") LocalIntRef sectionY) {
        if (section == null) {
            section = Objects.requireNonNull(protoChunk.getSection(sectionY.get()));
            section.lock();
        }
        return section;
    }
}
