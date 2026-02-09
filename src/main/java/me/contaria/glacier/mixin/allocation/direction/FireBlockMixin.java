package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.optimization.allocation.direction.DirectionValues;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Redirect(
            method = {
                    "getStateForPosition",
                    "areBlocksAroundFlammable",
                    "getBurnChance(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)I"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private Direction[] reuseDirectionValues() {
        return DirectionValues.DIRECTIONS;
    }
}
