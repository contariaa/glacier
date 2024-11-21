package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.Glacier;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ConcretePowderBlock.class)
public abstract class ConcretePowderBlockMixin {

    @Redirect(
            method = "hardensOnAnySide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private static Direction[] reuseDirectionValues() {
        return Glacier.DIRECTIONS;
    }
}
