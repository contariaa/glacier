package me.contaria.glacier.optimization.memory.structures;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Objects;

/**
 * Iterates over the entries in a {@link GlacierStructureBlockInfoList} by creating a {@link MutableInfo}
 * which gets populated with the current entry during iteration.
 */
public class GlacierStructureBlockInfoIterator implements Iterator<Structure.StructureBlockInfo> {
    private final GlacierStructureBlockInfoList infos;
    private final MutableInfo info;

    private final int xOffset, yOffset, zOffset, stateOffset;
    private final int xMask, yMask, zMask, stateMask, tagMask;

    private int index = 0;

    public GlacierStructureBlockInfoIterator(GlacierStructureBlockInfoList infos) {
        this.infos = infos;
        this.info = new MutableInfo(new BlockPos.Mutable(), null, null);
        this.xOffset = infos.xBits;
        this.yOffset = this.xOffset + infos.yBits;
        this.zOffset = this.yOffset + infos.zBits;
        this.stateOffset = this.zOffset + infos.stateBits;
        this.xMask = (1 << infos.xBits) - 1;
        this.yMask = (1 << infos.yBits) - 1;
        this.zMask = (1 << infos.zBits) - 1;
        this.stateMask = (1 << infos.stateBits) - 1;
        this.tagMask = (1 << infos.tagBits) - 1;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.size();
    }

    @Override
    public Structure.StructureBlockInfo next() {
        int index = this.toIndex(this.index++);
        if (index >= this.infos.size) {
            throw new IndexOutOfBoundsException();
        }

        int bitsPerEntry = this.infos.bitsPerEntry;
        if (bitsPerEntry == 0) {
            // the position is guaranteed to be {0, 0, 0}
            ((MutableStructureBlockInfo) this.info).glacier$set(
                    0, 0, 0,
                    this.infos.states[0],
                    this.infos.tags[0]
            );
            return this.info;
        }

        int entriesPerLong = 64 / bitsPerEntry;
        long entry = this.infos.data[index / entriesPerLong] >>> ((index % entriesPerLong) * bitsPerEntry);

        int x = (int) (entry & this.xMask);
        int y = (int) (entry >> this.xOffset & this.yMask);
        int z = (int) (entry >> this.yOffset & this.zMask);
        int state = (int) (entry >> this.zOffset & this.stateMask);
        int tag = (int) (entry >> this.stateOffset & this.tagMask);

        ((MutableStructureBlockInfo) this.info).glacier$set(
                x, y, z,
                this.infos.states[state],
                this.infos.tags[tag]
        );
        return this.info;
    }

    /**
     * @return The index in the backing {@link GlacierStructureBlockInfoList} corresponding to the given index of this iterator.
     */
    protected int toIndex(int index) {
        return index;
    }

    /**
     * @return The size of this iterator.
     */
    protected int size() {
        return this.infos.size;
    }

    public static class MutableInfo extends Structure.StructureBlockInfo {
        public MutableInfo(BlockPos.Mutable mutable, BlockState state, @Nullable CompoundTag tag) {
            super(mutable, state, tag);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MutableInfo info = (MutableInfo) o;
            return Objects.equals(this.state, info.state) && Objects.equals(this.tag, info.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.state, this.tag);
        }
    }
}
