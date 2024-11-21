package me.contaria.glacier.mixin.allocation.blockpos.block;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.CactusBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin {

    @Inject(
            method = "canPlaceAt",
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfoReturnable<Boolean> cir, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "canPlaceAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, Direction direction, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos, direction);
    }

    @Redirect(
            method = "canPlaceAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPosDown(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX(), pos.getY() - 1, pos.getZ());
    }

    @Redirect(
            method = "canPlaceAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPosUp(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX(), pos.getY() - 1, pos.getZ());
    }
}
