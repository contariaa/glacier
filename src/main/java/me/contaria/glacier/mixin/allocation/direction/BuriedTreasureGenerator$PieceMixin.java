package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.Glacier;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Glacier overwrites this method in mixin.gen.structure.BuriedTreasureGenerator$PieceMixin
@Mixin(value = BuriedTreasureGenerator.Piece.class, priority = 1500)
public abstract class BuriedTreasureGenerator$PieceMixin {

    @Redirect(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private Direction[] reuseDirectionValues() {
        return Glacier.DIRECTIONS;
    }
}
