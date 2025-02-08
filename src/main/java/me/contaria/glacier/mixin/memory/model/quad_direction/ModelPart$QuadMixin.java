package me.contaria.glacier.mixin.memory.model.quad_direction;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Reuse existing {@link Vector3f} constants instead of allocating new instances.
 */
@Mixin(targets = "net/minecraft/client/model/ModelPart$Quad")
public abstract class ModelPart$QuadMixin {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;getUnitVector()Lnet/minecraft/client/util/math/Vector3f;"
            )
    )
    private Vector3f useExistingVectors(Direction direction, @Local(argsOnly = true) boolean flip) {
        switch (direction) {
            case DOWN:
                return Vector3f.NEGATIVE_Y;
            case UP:
                return Vector3f.POSITIVE_Y;
            case NORTH:
                return Vector3f.NEGATIVE_Z;
            case SOUTH:
                return Vector3f.POSITIVE_Z;
            case WEST:
                return flip ? Vector3f.POSITIVE_X : Vector3f.NEGATIVE_X;
            case EAST:
                return flip ? Vector3f.NEGATIVE_X : Vector3f.POSITIVE_X;
        }
        return null;
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/Vector3f;multiplyComponentwise(FFF)V"
            )
    )
    private void alreadyFlipped(Vector3f vector3f, float x, float y, float z) {
    }
}
