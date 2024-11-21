package me.contaria.glacier.optimization.memory.biome_array_data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;

public class GlacierBiomeArray extends BiomeArray {
    private final BiomeArrayData data;

    public GlacierBiomeArray(BiomeArrayData data) {
        super((Biome[]) null);
        this.data = data;
    }

    public GlacierBiomeArray(ChunkPos pos, BiomeSource source) {
        this(BiomeArrayData.create(pos, source, DEFAULT_LENGTH));
    }

    public GlacierBiomeArray(PacketByteBuf buf) {
        this(BiomeArrayData.create(buf, DEFAULT_LENGTH));
    }

    public GlacierBiomeArray(ChunkPos pos, BiomeSource source, int[] rawIds) {
        this(rawIds != null ? BiomeArrayData.create(rawIds) : BiomeArrayData.create(pos, source, DEFAULT_LENGTH));
    }

    @Override
    public int[] toIntArray() {
        return this.data.toIntArray();
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        this.data.toPacket(buf);
    }

    @Override
    public BiomeArray copy() {
        return new GlacierBiomeArray(this.data.copy());
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.data.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }
}
