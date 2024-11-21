package me.contaria.glacier.mixin.allocation.blockpos.gen.feature;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.feature.CoralFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CoralFeature.class)
public abstract class CoralFeatureMixin {

    @Redirect(
            method = "spawnCoralPiece",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos createMutableBlockPos(BlockPos pos) {
        return new BlockPos.Mutable(pos.getX(), pos.getY() + 1, pos.getZ());
    }

    @Redirect(
            method = "spawnCoralPiece",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, Direction direction, @Local(ordinal = 1) BlockPos mutable) {
        return ((BlockPos.Mutable) mutable).set(pos, direction);
    }
}
