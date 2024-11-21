package me.contaria.glacier.mixin.allocation.blockpos.world;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.contaria.glacier.Glacier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkTickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkTickScheduler.class)
public abstract class ChunkTicketSchedulerMixin {

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfo ci, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/ProtoChunk;joinBlockPos(SILnet/minecraft/util/math/ChunkPos;)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(short sectionRel, int sectionY, ChunkPos chunkPos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return Glacier.joinBlockPos(mutable.get(), sectionRel, sectionY, chunkPos);
    }
}
