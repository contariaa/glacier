package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net/minecraft/world/biome/layer/ScaleLayer$1")
public abstract class ScaleLayer$FUZZYMixin extends ScaleLayerMixin {

    @Override
    protected int sample(LayerSampleContext<?> context, LayerSampler parent, int a, int bx, int bz, int cx, int cz, int dx, int dz) {
        int i = context.nextInt(4);
        if (i == 0) {
            return a;
        }
        if (i == 1) {
            return parent.sample(bx, bz);
        }
        if (i == 2) {
            return parent.sample(cx, cz);
        }
        return parent.sample(dx, dz);
    }
}
