package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.Glacier;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerEntity.class)
public abstract class ShulkerEntityMixin {

    @Redirect(
            method = "findAttachSide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private Direction[] reuseDirectionValues() {
        return Glacier.DIRECTIONS;
    }
}
