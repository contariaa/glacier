package me.contaria.glacier.optimization.memory.structure_block_infos;

import net.minecraft.block.Block;
import net.minecraft.structure.Structure;

import java.util.Collections;
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
                block, blockx -> {
                    GlacierStructureBlockInfoList list;
                    try {
                        list = new GlacierStructureBlockInfoList(this.infos, info -> info.state.isOf(blockx));
                    } catch (GlacierCompressionException e) {
                        throw new RuntimeException("Compression of filtered list failed even though parent list compression succeeded?!", e);
                    }
                    if (list.isEmpty()) {
                        return Collections.emptyList();
                    }
                    return list;
                }
        );
    }
}
