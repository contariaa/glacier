package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.optimization.allocation.direction.DirectionValues;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin {

    @Redirect(
            method = "updateDistanceFromLogs",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private static Direction[] reuseDirectionValues() {
        return DirectionValues.DIRECTIONS;
    }
}
