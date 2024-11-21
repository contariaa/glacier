package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.Glacier;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RedstoneOreBlock.class)
public abstract class RedstoneOreBlockMixin {

    @Redirect(
            method = "spawnParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private static Direction[] reuseDirectionValues() {
        return Glacier.DIRECTIONS;
    }
}
