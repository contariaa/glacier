package me.contaria.glacier.mixin.debug;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.contaria.glacier.Glacier;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = DebugHud.class, priority = 1500)
public abstract class DebugHudMixin {

    @ModifyReturnValue(
            method = "getRightText",
            at = @At("RETURN")
    )
    private List<String> addGlacierDebugText(List<String> debugText) {
        debugText.add("");
        debugText.add("Glacier v" + Glacier.VERSION);
        return debugText;
    }
}
