package me.contaria.glacier.mixin.lazier_dfu;

import net.minecraft.util.Util;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Util.class)
public abstract class UtilMixin {

    @Redirect(
            method = "getChoiceType",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/SharedConstants;useChoiceTypeRegistrations:Z",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private static boolean cancelChoiceTypeRegistrations() {
        // Util#getChoiceType is only used when building EntityTypes and BlockEntityTypes
        // the return value is always unused, the call is probably meant to make sure everything is registered correctly
        return false;
    }
}
