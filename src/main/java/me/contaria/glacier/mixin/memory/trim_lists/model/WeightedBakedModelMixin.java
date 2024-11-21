package me.contaria.glacier.mixin.memory.trim_lists.model;

import net.minecraft.client.render.model.WeightedBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(WeightedBakedModel.class)
public abstract class WeightedBakedModelMixin {

    @Inject(
            method = "<init>(Ljava/util/List;)V",
            at = @At("TAIL")
    )
    private void trimInfos(List<?> models, CallbackInfo ci) {
        ((ArrayList<?>) models).trimToSize();
    }
}
