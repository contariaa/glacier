package me.contaria.glacier.mixin.memory.trim_lists.model;

import net.minecraft.client.render.model.MultipartBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultipartBakedModel.class)
public abstract class MultipartBakedModelMixin {

    @Inject(
            method = "<init>(Ljava/util/List;)V",
            at = @At("TAIL")
    )
    private void trimComponents(List<?> components, CallbackInfo ci) {
        ((ArrayList<?>) components).trimToSize();
    }
}
