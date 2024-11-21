package me.contaria.glacier.mixin.memory.model.model_transformation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelTransformation.Deserializer.class)
public abstract class ModelTransformation$DeserializerMixin {

    @WrapOperation(
            method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/ModelTransformation;",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/client/render/model/json/Transformation;Lnet/minecraft/client/render/model/json/Transformation;Lnet/minecraft/client/render/model/json/Transformation;Lnet/minecraft/client/render/model/json/Transformation;Lnet/minecraft/client/render/model/json/Transformation;Lnet/minecraft/client/render/model/json/Transformation;Lnet/minecraft/client/render/model/json/Transformation;Lnet/minecraft/client/render/model/json/Transformation;)Lnet/minecraft/client/render/model/json/ModelTransformation;"
            )
    )
    private ModelTransformation useModelTransformationNone(Transformation thirdPersonLeftHand, Transformation thirdPersonRightHand, Transformation firstPersonLeftHand, Transformation firstPersonRightHand, Transformation head, Transformation gui, Transformation ground, Transformation fixed, Operation<ModelTransformation> original) {
        if (thirdPersonLeftHand == Transformation.IDENTITY &&
                thirdPersonRightHand == Transformation.IDENTITY &&
                firstPersonLeftHand == Transformation.IDENTITY &&
                firstPersonRightHand == Transformation.IDENTITY &&
                head == Transformation.IDENTITY &&
                gui == Transformation.IDENTITY &&
                ground == Transformation.IDENTITY &&
                fixed == Transformation.IDENTITY
        ) {
            return ModelTransformation.NONE;
        }
        return original.call(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed);
    }
}
