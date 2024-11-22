package me.contaria.glacier.mixin.memory.trim_lists.model;

import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ModelOverrideList.class)
public abstract class ModelOverrideListMixin {
    @Shadow
    @Final
    private List<ModelOverride> overrides;

    @Inject(
            method = "<init>(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Ljava/util/List;)V",
            at = @At("TAIL")
    )
    private void trimOverrides(CallbackInfo ci) {
        ((ArrayList<?>) this.overrides).trimToSize();
    }
}
