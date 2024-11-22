package me.contaria.glacier.config;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GlacierConfig {
    private final Object2BooleanMap<String> rules = new Object2BooleanArrayMap<>();
    private final File file;

    public GlacierConfig(File file) throws IOException {
        this.file = file;

        this.init();
        this.read();
        this.write();
    }

    private void init() {
        this.addRule("allocation");
        this.addRule("allocation.blockpos");
        this.addRule("allocation.blockpos.block");
        this.addRule("allocation.blockpos.fluid");
        this.addRule("allocation.blockpos.gen");
        this.addRule("allocation.blockpos.structure");
        this.addRule("allocation.blockpos.world");
        this.addRule("allocation.carver");
        this.addRule("allocation.carving_bitmask");
        this.addRule("allocation.direction");

        this.addRule("chunk");
        this.addRule("chunk.create_future");
        this.addRule("chunk.initial_capacity");

        this.addRule("gen");
        this.addRule("gen.bedrock");
        this.addRule("gen.biome_layers");
        this.addRule("gen.carver");
        this.addRule("gen.feature");
        this.addRule("gen.noise");
        this.addRule("gen.structure");

        this.addRule("lazier_dfu");

        this.addRule("memory");
        this.addRule("memory.chunk");
        this.addRule("memory.chunk.biome_array_data");
        this.addRule("memory.chunk.chunk_sections");
        this.addRule("memory.chunk.entity_sections");
        this.addRule("memory.chunk.structure_references");
        this.addRule("memory.deduplicate_mappings");
        this.addRule("memory.empty_arrays");
        this.addRule("memory.entity");
        this.addRule("memory.entity.goal_selectors");
        this.addRule("memory.entity.item_stack");
        this.addRule("memory.mc_namespace");
        this.addRule("memory.model");
        this.addRule("memory.model.model_transformation");
        this.addRule("memory.model.quad_direction");
        this.addRule("memory.model.sprite_animation");
        this.addRule("memory.model.voxel_array");
        this.addRule("memory.trim_lists");
    }

    private void addRule(String mixin) {
        this.addRule(mixin, true);
    }

    private void addRule(String mixin, boolean enabled) {
        this.rules.put("mixin." + mixin, enabled);
    }

    private void setRule(String rule, boolean enabled) {
        this.rules.replace(rule, enabled);
    }

    public boolean getRule(String path) {
        String mixin = "mixin";
        for (String string : path.split("\\.")) {
            mixin += "." + string;
            if (!this.rules.getOrDefault(mixin, true)) {
                return false;
            }
        }
        return true;
    }

    private void read() throws IOException {
        if (!this.file.exists()) {
            return;
        }

        for (String line : Files.readAllLines(this.file.toPath())) {
            String[] split = line.split("=", 2);
            if (split.length < 2) {
                continue;
            }
            this.setRule(split[0].trim(), Boolean.parseBoolean(split[1].trim()));
        }
    }

    private void write() throws IOException {
        if (!this.file.exists() && !this.file.createNewFile()) {
            throw new RuntimeException("Failed to create Glacier config file!");
        }

        List<String> lines = new ArrayList<>();
        for (Object2BooleanMap.Entry<String> entry : this.rules.object2BooleanEntrySet()) {
            lines.add(entry.getKey() + "=" + entry.getBooleanValue());
        }
        Files.write(this.file.toPath(), lines);
    }
}
