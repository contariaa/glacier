package me.contaria.glacier.mixin.chunk.create_future;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import me.contaria.glacier.optimization.chunk.create_future.UnloadedChunkHolderAt;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {

    @Shadow
    @Nullable
    protected abstract ChunkHolder getCurrentChunkHolder(long pos);

    @Shadow
    protected abstract Either<List<Chunk>, ChunkHolder.Unloaded> method_17215(int par1, int par2, int par3, List<Either<Chunk, ChunkHolder.Unloaded>> par4);

    @ModifyArg(
            method = "generateChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;createChunkRegionFuture(Lnet/minecraft/util/math/ChunkPos;ILjava/util/function/IntFunction;)Ljava/util/concurrent/CompletableFuture;"
            ),
            index = 2
    )
    private IntFunction<ChunkStatus> precomputeDistanceToStatus(IntFunction<ChunkStatus> distanceToStatus, @Local(argsOnly = true) ChunkStatus status) {
        ChunkStatus[] statuses = new ChunkStatus[status.getTaskMargin() + 1];
        for (int i = 0; i < statuses.length; i++) {
            statuses[i] = distanceToStatus.apply(i);
        }
        return i -> statuses[i];
    }

    /**
     * @author contaria
     * @reason Simplify and set initial capacity for list.
     */
    @Overwrite
    private CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> createChunkRegionFuture(ChunkPos centerChunk, int margin, IntFunction<ChunkStatus> distanceToStatus) {
        List<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> futures = new ArrayList<>((margin * 2 + 1) * (margin * 2 + 1));
        int x = centerChunk.x;
        int z = centerChunk.z;

        for (int xOffset = -margin; xOffset <= margin; xOffset++) {
            for (int zOffset = -margin; zOffset <= margin; zOffset++) {
                ChunkHolder holder = this.getCurrentChunkHolder(ChunkPos.toLong(x + zOffset, z + xOffset));
                if (holder == null) {
                    return CompletableFuture.completedFuture(Either.right(new UnloadedChunkHolderAt(x + zOffset, z + xOffset)));
                }
                futures.add(holder.createFuture(
                        distanceToStatus.apply(Math.max(Math.abs(zOffset), Math.abs(xOffset))),
                        (ThreadedAnvilChunkStorage) (Object) this
                ));
            }
        }

        return Util.combine(futures).thenApply(results -> this.method_17215(x, margin, z, results));
    }
}
