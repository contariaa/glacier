package me.contaria.glacier.mixin.gen.structure;

import net.minecraft.block.entity.JigsawBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(JigsawBlockEntity.Joint.class)
public abstract class JigsawBlockEntity$JointMixin {

    /**
     * @author contaria
     * @reason Use if-statements instead of streams
     */
    @Overwrite
    public static Optional<JigsawBlockEntity.Joint> byName(String name) {
        if ("rollable".equals(name)) {
            return Optional.of(JigsawBlockEntity.Joint.ROLLABLE);
        }
        if ("aligned".equals(name)) {
            return Optional.of(JigsawBlockEntity.Joint.ALIGNED);
        }
        return Optional.empty();
    }
}
