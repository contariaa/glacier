package me.contaria.glacier.config;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GlacierMixinConfigPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("Glacier MixinConfigPlugin");

    private GlacierConfig config;
    private String mixinPackage;

    @Override
    public void onLoad(String mixinPackage) {
        this.mixinPackage = mixinPackage;

        try {
            this.config = new GlacierConfig(FabricLoader.getInstance().getConfigDir().resolve("glacier.properties").toFile());
        } catch (Exception e) {
            throw new RuntimeException("Could not load configuration file for Glacier", e);
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixin = mixinClassName.substring(this.mixinPackage.length() + 1);
        if (mixin.startsWith("compat.")) {
            return FabricLoader.getInstance().isModLoaded(mixin.substring(7, mixin.indexOf('.', 7)));
        }
        if (!this.config.getRule(mixin)) {
            LOGGER.warn("Disabling mixin '{}' disabled by Glacier config.", mixinClassName);
            return false;
        }
        return true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
