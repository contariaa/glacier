package me.contaria.glacier.optimization.memory.biome_array_data;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A paletted implementation of {@link BiomeArrayData} to compress a {@link Biome} array.
 * <p>
 * {@link PalettedBiomeArrayData#data} represents the biomes in a compressed view as a long array.
 * Each biome takes up a certain amount of bits in the longs, with those bits corresponding to the
 * index of the biome in the {@link PalettedBiomeArrayData#biomes} palette.
 * <p>
 * Each biome takes up as little bits as possible based on the amount of different biomes, so:
 * <p>
 *   2 biomes -> 1 bit,
 *   3-4 biomes -> 2 bits,
 *   5-8 biomes -> 3 bits,
 *   etc.
 * <p>
 * If the amount of biomes in the palette is not a power of 2, the trailing bits of each long will be unused.
 * <p>
 * The total amount of biomes represented is stored in {@link PalettedBiomeArrayData#length}.
 * The amount of bits each biome takes up is stored in {@link PalettedBiomeArrayData#bitsPerEntry}.
 * The amount of biomes per {@code long} is stored in {@link PalettedBiomeArrayData#entriesPerLong} and equal to {@code 64 / bitsPerEntry}.
 */
public class PalettedBiomeArrayData implements BiomeArrayData {
    private final Biome[] biomes;
    private final int length;
    private final long[] data;
    private final int bitsPerEntry;
    private final int entriesPerLong;

    public PalettedBiomeArrayData(Biome[] biomes, int length, long[] data, int bitsPerEntry) {
        this.biomes = biomes;
        this.length = length;
        this.data = data;
        this.bitsPerEntry = bitsPerEntry;
        this.entriesPerLong = 64 / bitsPerEntry;
    }

    @Override
    public int[] toIntArray() {
        int[] rawIDs = this.getRawBiomeIDs();
        int[] intArray = new int[this.length];
        for (int i = 0; i < this.length; i++) {
            intArray[i] = rawIDs[this.getBiomeIndex(i)];
        }
        return intArray;
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        int[] rawIDs = this.getRawBiomeIDs();
        for (int i = 0; i < this.length; i++) {
            buf.writeInt(rawIDs[this.getBiomeIndex(i)]);
        }
    }

    @Override
    public BiomeArrayData copy() {
        return new PalettedBiomeArrayData(Arrays.copyOf(this.biomes, this.biomes.length), this.length, Arrays.copyOf(this.data, this.data.length), this.bitsPerEntry);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int x = biomeX & BiomeArray.HORIZONTAL_BIT_MASK;
        int y = MathHelper.clamp(biomeY, 0, BiomeArray.VERTICAL_BIT_MASK);
        int z = biomeZ & BiomeArray.HORIZONTAL_BIT_MASK;
        return this.getBiome(y << BiomeArray.HORIZONTAL_SECTION_COUNT + BiomeArray.HORIZONTAL_SECTION_COUNT | z << BiomeArray.HORIZONTAL_SECTION_COUNT | x);
    }

    /**
     * @return The biome corresponding to the given index of the represented array.
     */
    private Biome getBiome(int index) {
        return this.biomes[this.getBiomeIndex(index)];
    }

    /**
     * @return The biomes index in the {@link PalettedBiomeArrayData#biomes} palette corresponding to the given index of the represented array.
     */
    private int getBiomeIndex(int index) {
        return readData(this.data, index, this.bitsPerEntry, this.entriesPerLong, (1 << this.bitsPerEntry) - 1);
    }

    /**
     * @return The {@link PalettedBiomeArrayData#biomes} palette converted to an integer array of raw biome id's.
     */
    private int[] getRawBiomeIDs() {
        int[] rawIDs = new int[this.biomes.length];
        for (int i = 0; i < rawIDs.length; i++) {
            rawIDs[i] = Registry.BIOME.getRawId(this.biomes[i]);
        }
        return rawIDs;
    }

    /**
     * @param startX The corresponding {@link Chunk}'s startX in biome coordinates.
     * @param startZ The corresponding {@link Chunk}'s startZ in biome coordinates.
     * @param source The {@link BiomeSource} providing the biomes.
     * @param length The total amount of biomes to represent.
     * @param secondBiomeIndex The index at which the {@code secondBiome} was found.
     * @param firstBiome The first biome found from indices {@code 0} to {@code secondBiomeIndex - 1}.
     * @param secondBiome The second biome found at index {@code secondBiomeIndex}
     *
     * @return A {@link PalettedBiomeArrayData} from a {@link BiomeSource}.
     */
    static BiomeArrayData create(int startX, int startZ, BiomeSource source, int length, int secondBiomeIndex, Biome firstBiome, Biome secondBiome) {
        List<Biome> biomes = new ArrayList<>();
        biomes.add(firstBiome);
        biomes.add(secondBiome);
        int bitsPerEntry = 1;
        int entriesPerLong = 64;
        long[] data = new long[(length + entriesPerLong - 1) / entriesPerLong];
        for (int i = secondBiomeIndex; i < length; i++) {
            int x = i & BiomeArray.HORIZONTAL_BIT_MASK;
            int y = i >> BiomeArray.HORIZONTAL_SECTION_COUNT + BiomeArray.HORIZONTAL_SECTION_COUNT & BiomeArray.VERTICAL_BIT_MASK;
            int z = i >> BiomeArray.HORIZONTAL_SECTION_COUNT & BiomeArray.HORIZONTAL_BIT_MASK;
            Biome biome = source.getBiomeForNoiseGen(startX + x, y, startZ + z);
            int biomeIndex = biomes.indexOf(biome);
            if (biomeIndex == -1) {
                biomes.add(biome);
                // resize the data array if more
                if (biomes.size() > (1 << bitsPerEntry)) {
                    data = resize(data, length, bitsPerEntry, bitsPerEntry + 1, secondBiomeIndex, i);
                    bitsPerEntry++;
                    entriesPerLong = 64 / bitsPerEntry;
                }
                biomeIndex = biomes.indexOf(biome);
            }
            writeData(data, i, biomeIndex, bitsPerEntry, entriesPerLong);
        }
        return new PalettedBiomeArrayData(biomes.toArray(new Biome[0]), length, data, bitsPerEntry);
    }

    /**
     * @param buf The {@link PacketByteBuf} providing the biomes.
     * @param length The total amount of biomes to represent.
     * @param secondBiomeIndex The index at which the {@code secondBiome} was found.
     * @param firstBiome The first biome found from indices {@code 0} to {@code secondBiomeIndex - 1}.
     * @param secondBiome The second biome found at index {@code secondBiomeIndex}
     *
     * @return A {@link PalettedBiomeArrayData} from a {@link PacketByteBuf}.
     */
    static BiomeArrayData create(PacketByteBuf buf, int length, int secondBiomeIndex, int firstBiome, int secondBiome) {
        IntList biomes = new IntArrayList();
        biomes.add(firstBiome);
        biomes.add(secondBiome);
        int bitsPerEntry = 1;
        int entriesPerLong = 64;
        long[] data = new long[(length + entriesPerLong - 1) / entriesPerLong];
        for (int i = secondBiomeIndex; i < length; i++) {
            int biome = buf.readInt();
            int biomeIndex = biomes.indexOf(biome);
            if (biomeIndex == -1) {
                biomes.add(biome);
                if (biomes.size() > 1 << bitsPerEntry) {
                    data = resize(data, length, bitsPerEntry, bitsPerEntry + 1, secondBiomeIndex, i);
                    bitsPerEntry++;
                    entriesPerLong = 64 / bitsPerEntry;
                }
                biomeIndex = biomes.indexOf(biome);
            }
            writeData(data, i, biomeIndex, bitsPerEntry, entriesPerLong);
        }
        return new PalettedBiomeArrayData(toBiomeArray(biomes), length, data, bitsPerEntry);
    }

    /**
     * @param ids The raw biome id's.
     * @param secondBiomeIndex The index at which the {@code secondBiome} was found.
     * @param firstBiome The first biome found from indices {@code 0} to {@code secondBiomeIndex - 1}.
     * @param secondBiome The second biome found at index {@code secondBiomeIndex}
     *
     * @return A {@link PalettedBiomeArrayData} from a {@link PacketByteBuf}.
     */
    static BiomeArrayData create(int[] ids, int secondBiomeIndex, int firstBiome, int secondBiome) {
        IntList biomes = new IntArrayList();
        biomes.add(firstBiome);
        biomes.add(secondBiome);
        int bitsPerEntry = 1;
        int entriesPerLong = 64;
        long[] data = new long[(ids.length + entriesPerLong - 1) / entriesPerLong];
        for (int i = secondBiomeIndex; i < ids.length; i++) {
            int biome = ids[i];
            int biomeIndex = biomes.indexOf(biome);
            if (biomeIndex == -1) {
                biomeIndex = biomes.size();
                biomes.add(biome);
                if (biomeIndex > 1 << bitsPerEntry) {
                    data = resize(data, ids.length, bitsPerEntry, bitsPerEntry + 1, secondBiomeIndex, i);
                    bitsPerEntry++;
                    entriesPerLong = 64 / bitsPerEntry;
                }
            }
            writeData(data, i, biomeIndex, bitsPerEntry, entriesPerLong);
        }
        return new PalettedBiomeArrayData(toBiomeArray(biomes), ids.length, data, bitsPerEntry);
    }

    /**
     * @return A new {@link Biome} array from the given raw id's.
     */
    private static Biome[] toBiomeArray(IntList ids) {
        Biome[] biomes = new Biome[ids.size()];
        for (int i = 0; i < biomes.length; i++) {
            biomes[i] = Registry.BIOME.get(ids.getInt(i));
        }
        return biomes;
    }

    /**
     * @param data The data to resize.
     * @param length The length of the represented array.
     * @param oldBitsPerEntry The bits per entry of the old data array.
     * @param newBitsPerEntry The bits per entry of the new array to be created.
     * @param fromIndex The index (of the represented array, not the compressed array) from which to start copying biomes into the resized array, everything before is the first biome and as such 0.
     * @param toIndex The index (of the represented array, not the compressed array) up to which to copy biomes into the resized array, everything after is not yet populated.
     * @return A copy of the given data array with the new amount of bits per entry.
     */
    private static long[] resize(long[] data, int length, int oldBitsPerEntry, int newBitsPerEntry, int fromIndex, int toIndex) {
        int oldEntriesPerLong = 64 / oldBitsPerEntry;
        int newEntriesPerLong = 64 / newBitsPerEntry;
        int oldBitMask = (1 << oldBitsPerEntry) - 1;
        long[] resized = new long[(length + newEntriesPerLong - 1) / newEntriesPerLong];
        for (int i = fromIndex; i < toIndex; i++) {
            writeData(resized, i, readData(data, i, oldBitsPerEntry, oldEntriesPerLong, oldBitMask), newBitsPerEntry, newEntriesPerLong);
        }
        return resized;
    }

    /**
     * Writes the value to the given index in the data array.
     *
     * @param data The data to write to.
     * @param index The index to write at.
     * @param value The value to write.
     * @param bitsPerEntry The bits per entry of the data array.
     * @param entriesPerLong The entries per long of the data array.
     */
    private static void writeData(long[] data, int index, int value, int bitsPerEntry, int entriesPerLong) {
        data[index / entriesPerLong] |= (long) value << ((index % entriesPerLong) * bitsPerEntry);
    }

    /**
     * Reads the value from the given index in the data array.
     *
     * @param data The data to read from.
     * @param index The index to read at.
     * @param bitsPerEntry The bits per entry of the data array.
     * @param entriesPerLong The entries per long of the data array.
     *
     * @return The value read from the data array at the given index.
     */
    private static int readData(long[] data, int index, int bitsPerEntry, int entriesPerLong, int bitMask) {
        return (int) (data[index / entriesPerLong] >> ((index % entriesPerLong) * bitsPerEntry)) & bitMask;
    }
}
