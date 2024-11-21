package me.contaria.glacier.mixin.memory.deduplicate_mappings;

import me.contaria.glacier.optimization.memory.deduplicate_mappings.DeduplicateMappings;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MinecraftDedicatedServerMixin {

    @Inject(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;loadWorld()V"))
    private void onPostLaunchEntrypoint(CallbackInfoReturnable<Boolean> cir) {
        DeduplicateMappings.tryDeduplicateMappings();
    }
}
