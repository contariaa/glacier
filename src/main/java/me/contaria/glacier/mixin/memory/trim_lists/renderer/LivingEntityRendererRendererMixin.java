package me.contaria.glacier.mixin.memory.trim_lists.renderer;

import me.contaria.glacier.optimization.memory.trim_lists.GlacierTrimmable;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererRendererMixin implements GlacierTrimmable {
    @Shadow
    @Final
    protected List<FeatureRenderer<?, ?>> features;

    @Override
    public void glacier$trim() {
        ((ArrayList<?>) this.features).trimToSize();
    }
}
