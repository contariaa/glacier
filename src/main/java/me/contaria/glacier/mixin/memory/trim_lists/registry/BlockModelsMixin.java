package me.contaria.glacier.mixin.memory.trim_lists.registry;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BlockModels.class)
public abstract class BlockModelsMixin {

    @Shadow
    @Final
    private Map<BlockState, BakedModel> models;

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/BlockModels;models:Ljava/util/Map;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void replaceIdentityHashMap(BlockModels models, Map<BlockState, BakedModel> value, Operation<Void> original) {
        original.call(models, new Reference2ReferenceOpenHashMap<>());
    }

    @Inject(
            method = "reload",
            at = @At("TAIL")
    )
    private void trimModels(CallbackInfo ci) {
        ((Reference2ReferenceOpenHashMap<BlockState, BakedModel>) this.models).trim();
    }
}
