package me.contaria.glacier.mixin.lazier_dfu;

import com.mojang.datafixers.DataFixer;
import me.contaria.glacier.optimization.lazier_dfu.LazyDataFixer;
import net.minecraft.datafixer.Schemas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @see LazyDataFixer
 */
@Mixin(Schemas.class)
public abstract class SchemasMixin {

    @Shadow
    private static DataFixer create() {
        return null;
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/datafixer/Schemas;create()Lcom/mojang/datafixers/DataFixer;"
            )
    )
    private static DataFixer lazilyInitializeDataFixer() {
        return new LazyDataFixer(SchemasMixin::create);
    }
}
