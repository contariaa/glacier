package me.contaria.glacier.mixin.allocation.direction;

import me.contaria.glacier.Glacier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.UpgradeData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(UpgradeData.class)
public abstract class UpgradeDataMixin {

    @Redirect(
            method = "upgradeCenter",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private Direction[] reuseDirectionValues() {
        return Glacier.DIRECTIONS;
    }

    @Redirect(
            method = "upgradeSide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private static Direction[] reuseDirectionValues2() {
        return Glacier.DIRECTIONS;
    }
}
