package me.contaria.glacier.mixin.gen.biome_layers;

import me.contaria.glacier.optimization.gen.biome_layers.GlacierCachingLayerContext;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ScaleLayer.class)
public abstract class ScaleLayerMixin implements ParentedLayer {

    @Shadow
    protected abstract int sample(LayerSampleContext<?> context, int a, int b, int c, int d);

    /**
     * @author contaria
     * @reason Reduce sampling
     */
    @Overwrite
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int sampleXZ = parent.sample(this.transformX(x), this.transformZ(z));
        boolean xEven = (x & 1) == 0;
        boolean zEven = (z & 1) == 0;
        if (xEven && zEven) {
            return sampleXZ;
        }

        context.initSeed(x >> 1 << 1, z >> 1 << 1);

        if (xEven) {
            return ((GlacierCachingLayerContext) context).glacier$nextBoolean() ? sampleXZ : parent.sample(this.transformX(x), this.transformZ(z + 1));
        }
        // we skip the context.choose call
        ((GlacierCachingLayerContext) context).glacier$skipInt();

        if (zEven) {
            return ((GlacierCachingLayerContext) context).glacier$nextBoolean() ? sampleXZ : parent.sample(this.transformX(x + 1), this.transformZ(z));
        }
        // we skip the context.choose call
        ((GlacierCachingLayerContext) context).glacier$skipInt();

        return this.sample(
                context,
                parent,
                sampleXZ,
                this.transformX(x + 1), this.transformZ(z),
                this.transformX(x), this.transformZ(z + 1),
                this.transformX(x + 1), this.transformZ(z + 1)
        );
    }

    @Unique
    protected int sample(LayerSampleContext<?> context, LayerSampler parent, int a, int bx, int bz, int cx, int cz, int dx, int dz) {
        return this.sample(context, a, parent.sample(bx, bz), parent.sample(cx, cz), parent.sample(dx, dz));
    }
}
