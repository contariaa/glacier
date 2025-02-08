package me.contaria.glacier.mixin.memory.mc_namespace;

import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Reuse the same {@code "minecraft"} String for all mc namespace Identifiers.
 */
@Mixin(Identifier.class)
public abstract class IdentifierMixin {

    @Redirect(
            method = "<init>([Ljava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/commons/lang3/StringUtils;isEmpty(Ljava/lang/CharSequence;)Z",
                    remap = false
            )
    )
    private boolean deduplicateMinecraftNamespace(CharSequence cs) {
        return StringUtils.isEmpty(cs) || "minecraft".contentEquals(cs);
    }
}
