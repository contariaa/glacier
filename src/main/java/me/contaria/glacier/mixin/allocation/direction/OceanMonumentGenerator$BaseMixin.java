package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.optimization.allocation.direction.DirectionValues;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OceanMonumentGenerator.Base.class)
public abstract class OceanMonumentGenerator$BaseMixin {

    @Redirect(
            method = "method_14760",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private Direction[] reuseDirectionValues() {
        return DirectionValues.DIRECTIONS;
    }
}
