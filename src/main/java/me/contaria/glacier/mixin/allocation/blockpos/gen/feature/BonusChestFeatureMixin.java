package me.contaria.glacier.mixin.allocation.blockpos.gen.feature;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.feature.BonusChestFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BonusChestFeature.class)
public abstract class BonusChestFeatureMixin {

    @Redirect(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, Direction direction, @Local BlockPos.Mutable mutable) {
        return mutable.set(pos, direction);
    }
}
