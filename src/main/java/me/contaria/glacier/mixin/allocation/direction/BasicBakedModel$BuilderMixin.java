package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.optimization.allocation.direction.DirectionValues;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BasicBakedModel.Builder.class)
public abstract class BasicBakedModel$BuilderMixin {

    @Redirect(
            method = "<init>(ZZZLnet/minecraft/client/render/model/json/ModelTransformation;Lnet/minecraft/client/render/model/json/ModelOverrideList;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private Direction[] reuseDirectionValues() {
        return DirectionValues.DIRECTIONS;
    }
}
