package me.contaria.glacier.mixin.allocation.blockpos.gen.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.IcePatchFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IcePatchFeature.class)
public abstract class IcePatchFeatureMixin {

    @ModifyVariable(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/IcePatchFeatureConfig;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockPos createMutableBlockPos(BlockPos pos) {
        return pos.mutableCopy();
    }

    @Redirect(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/IcePatchFeatureConfig;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos mutable) {
        ((BlockPos.Mutable) mutable).setY(mutable.getY() - 1);
        return mutable;
    }
}
