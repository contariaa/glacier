package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.optimization.allocation.direction.DirectionValues;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin {

    @Redirect(
            method = {
                    "renderSmooth",
                    "renderFlat",
                    "getQuadDimensions"
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
