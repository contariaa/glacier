package me.contaria.glacier.optimization.allocation.carving_bitmask;

import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.stream.IntStream;

/**
 * Specialized BitSet implementation for carving masks, splits up the set into sections that are initialized lazily.
 * <p>
 * Minecraft creates one BitSet with size 65536 per Carver (air, liquid) per ProtoChunk during chunk generation.
 * That is 16KB of data that is discarded once the chunk is fully generated.
 * Most of the BitSet is fully unused, on land the air mask has an occupancy of ~1%, while the liquid mask is completely empty. In the ocean the liquid mask uses ~0.5% and the air mask goes below 1%.
 * By splitting the BitSet into sections corresponding to the ChunkSections, we can reduce the memory footprint drastically.
 * <p>
 * Only methods used by Minecraft ({@link BitSet#get(int)}, {@link BitSet#set(int)}, {@link BitSet#length()} and {@link BitSet#toByteArray()}) are implemented, every other method will throw an UnsupportedOperationException.
 */
public class CarvingMask extends BitSet {
    private final BitSet[] bitSets;
    private final int capacity;
    private int length;

    public CarvingMask(int sections) {
        if (sections < 0) {
            throw new IllegalArgumentException();
        }
        this.bitSets = new BitSet[sections];
        this.capacity = sections * 4096;
    }

    @Override
    public boolean get(int bitIndex) {
        if (bitIndex < 0 || bitIndex >= this.capacity) {
            throw new IllegalArgumentException();
        }
        BitSet set = this.bitSets[bitIndex >>> 12];
        if (set == null) {
            return false;
        }
        return set.get(bitIndex & 4095);
    }

    @Override
    public void set(int bitIndex) {
        if (bitIndex < 0 || bitIndex >= this.capacity) {
            throw new IllegalArgumentException();
        }
        if (this.length <= bitIndex) {
            this.length = bitIndex + 1;
        }
        BitSet set = this.bitSets[bitIndex >>> 12];
        if (set == null) {
            set = this.bitSets[bitIndex >>> 12] = new BitSet(4096);
        }
        set.set(bitIndex & 4095);
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public byte @NotNull [] toByteArray() {
        byte[] bytes = new byte[this.length];
        for (int i = 0; i < this.length; i++) {
            if (this.get(i)) {
                bytes[i] = 1;
            }
        }
        return bytes;
    }

    @Override
    public long @NotNull [] toLongArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flip(int bitIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flip(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int bitIndex, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int fromIndex, int toIndex, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(int bitIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public BitSet get(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextSetBit(int fromIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextClearBit(int fromIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int previousSetBit(int fromIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int previousClearBit(int fromIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean intersects(@NotNull BitSet set) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int cardinality() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void and(@NotNull BitSet set) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void or(@NotNull BitSet set) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void xor(@NotNull BitSet set) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void andNot(@NotNull BitSet set) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public IntStream stream() {
        throw new UnsupportedOperationException();
    }
}
