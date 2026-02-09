package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.optimization.allocation.direction.DirectionValues;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DirectionTransformation.class)
public abstract class DirectionTransformationMixin {

    @Redirect(
            method = "map",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private Direction[] reuseDirectionValues() {
        return DirectionValues.DIRECTIONS;
    }
}
