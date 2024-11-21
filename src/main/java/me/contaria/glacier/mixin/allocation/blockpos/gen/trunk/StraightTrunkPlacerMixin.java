package me.contaria.glacier.mixin.allocation.blockpos.gen.trunk;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StraightTrunkPlacer.class)
public abstract class StraightTrunkPlacerMixin {

    @Redirect(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos createAndUseMutableBlockPos(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable(pos.getX(), pos.getY() - 1, pos.getZ()));
        return mutable.get();
    }

    @Redirect(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;up(I)Lnet/minecraft/util/math/BlockPos;",
                    ordinal = 0
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, int distance, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX(), pos.getY() + distance, pos.getZ());
    }
}
