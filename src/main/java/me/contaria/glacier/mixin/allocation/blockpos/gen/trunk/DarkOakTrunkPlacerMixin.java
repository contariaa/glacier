package me.contaria.glacier.mixin.allocation.blockpos.gen.trunk;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DarkOakTrunkPlacer.class)
public abstract class DarkOakTrunkPlacerMixin {

    @Inject(
            method = "generate",
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfoReturnable<Boolean> cir, @Share("mutable") LocalRef<BlockPos.Mutable> mutable, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        mutable.set(new BlockPos.Mutable());
        mutable2.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;east()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPosEast(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX() + 1, pos.getY(), pos.getZ());
    }

    @Redirect(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;south()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPosSouth(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX(), pos.getY(), pos.getZ() + 1);
    }

    @Redirect(
            method = "generate",
            at = @At(
                    value = "NEW",
                    target = "(III)Lnet/minecraft/util/math/BlockPos;",
                    ordinal = 0
            )
    )
    private BlockPos useMutableBlockPos(int x, int y, int z, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        return mutable2.get().set(x, y, z);
    }

    @Redirect(
            method = "generate",
            at = @At(
                    value = "NEW",
                    target = "(III)Lnet/minecraft/util/math/BlockPos;",
                    ordinal = 2
            )
    )
    private BlockPos useMutableBlockPos2(int x, int y, int z, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        return mutable2.get().set(x, y, z);
    }
}
