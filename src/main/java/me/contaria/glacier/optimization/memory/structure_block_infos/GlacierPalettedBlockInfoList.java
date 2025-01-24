package me.contaria.glacier.optimization.memory.structure_block_infos;

import net.minecraft.block.Block;
import net.minecraft.structure.Structure;

import java.util.List;

/**
 * @see GlacierStructureBlockInfoList
 */
public class GlacierPalettedBlockInfoList extends Structure.PalettedBlockInfoList {
    public GlacierPalettedBlockInfoList(List<Structure.StructureBlockInfo> infos) throws GlacierCompressionException {
        super(new GlacierStructureBlockInfoList(infos));
    }

    @Override
    public List<Structure.StructureBlockInfo> getAllOf(Block block) {
        return this.blockToInfos.computeIfAbsent(
                block, key -> ((GlacierStructureBlockInfoList) this.infos).filter(state -> state.isOf(key))
        );
    }
}
