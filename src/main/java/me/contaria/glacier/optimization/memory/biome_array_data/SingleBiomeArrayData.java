package me.contaria.glacier.optimization.memory.biome_array_data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.Arrays;

public class SingleBiomeArrayData implements BiomeArrayData {
    private final Biome biome;
    private final int length;

    public SingleBiomeArrayData(Biome biome, int length) {
        this.biome = biome;
        this.length = length;
    }

    @Override
    public int[] toIntArray() {
        int[] intArray = new int[this.length];
        Arrays.fill(intArray, Registry.BIOME.getRawId(this.biome));
        return intArray;
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        int biome = Registry.BIOME.getRawId(this.biome);
        for (int i = 0; i < this.length; i++) {
            buf.writeInt(biome);
        }
    }

    @Override
    public BiomeArrayData copy() {
        return new SingleBiomeArrayData(this.biome, this.length);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biome;
    }
}
