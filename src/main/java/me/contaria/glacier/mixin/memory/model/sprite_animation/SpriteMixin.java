package me.contaria.glacier.mixin.memory.model.sprite_animation;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(Sprite.class)
public abstract class SpriteMixin {

    @Unique
    private static final AnimationResourceMetadata DEFAULT_ANIMATION = new AnimationResourceMetadata(Collections.singletonList(new AnimationFrameResourceMetadata(0, -1)), 16, 16, 1, false);

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/util/List;IIIZ)Lnet/minecraft/client/resource/metadata/AnimationResourceMetadata;"
            )
    )
    private AnimationResourceMetadata useDefaultSpriteAnimation(List<AnimationFrameResourceMetadata> frames, int width, int height, int defaultFrameTime, boolean interpolate, Operation<AnimationResourceMetadata> original) {
        if (frames.size() == 1 && frames.get(0).usesDefaultFrameTime() && width == 16 && height == 16 && defaultFrameTime == 1 && !interpolate) {
            return DEFAULT_ANIMATION;
        }
        return new AnimationResourceMetadata(ImmutableList.copyOf(frames), width, height, defaultFrameTime, interpolate);
    }
}
