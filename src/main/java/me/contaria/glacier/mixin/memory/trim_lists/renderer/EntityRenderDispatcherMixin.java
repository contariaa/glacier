package me.contaria.glacier.mixin.memory.trim_lists.renderer;

import me.contaria.glacier.optimization.memory.trim_lists.GlacierTrimmable;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Inject(
            method = "register",
            at = @At("TAIL")
    )
    private void trimEntityRendererFeatures(EntityType<?> entityType, EntityRenderer<?> entityRenderer, CallbackInfo ci) {
        if (entityRenderer instanceof GlacierTrimmable) {
            ((GlacierTrimmable) entityRenderer).glacier$trim();
        }
    }
}
