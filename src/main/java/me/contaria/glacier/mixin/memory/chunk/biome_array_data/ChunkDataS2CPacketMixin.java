package me.contaria.glacier.mixin.memory.chunk.biome_array_data;

import me.contaria.glacier.optimization.memory.biome_array_data.GlacierBiomeArray;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.biome.source.BiomeArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkDataS2CPacket.class)
public abstract class ChunkDataS2CPacketMixin {

    @Redirect(
            method = "read",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/world/biome/source/BiomeArray;"
            )
    )
    private BiomeArray useGlacierBiomeArray(PacketByteBuf buf) {
        return new GlacierBiomeArray(buf);
    }
}
