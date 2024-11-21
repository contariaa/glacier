package me.contaria.glacier.mixin.memory.chunk.entity_sections;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.glacier.optimization.memory.entity_sections.EmptyTypeFilterableList;
import net.minecraft.entity.Entity;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Minecraft creates 16 instances of {@link TypeFilterableList} per chunk, even if there is no entities in the corresponding section.
 * Instead, we create these lists lazily, similar to how {@link ChunkSection}'s are loaded.
 */
@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    @Unique
    private static final TypeFilterableList<Entity> EMPTY_SECTION = new EmptyTypeFilterableList<>(Entity.class);

    @WrapOperation(
            method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/source/BiomeArray;Lnet/minecraft/world/chunk/UpgradeData;Lnet/minecraft/world/TickScheduler;Lnet/minecraft/world/TickScheduler;J[Lnet/minecraft/world/chunk/ChunkSection;Ljava/util/function/Consumer;)V",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/Class;)Lnet/minecraft/util/collection/TypeFilterableList;"
            )
    )
    private TypeFilterableList<Entity> lazilyCreateEntitySections(Class<Entity> elementType, Operation<TypeFilterableList<Entity>> original) {
        return EMPTY_SECTION;
    }

    @Definition(id = "entitySections", field = "Lnet/minecraft/world/chunk/WorldChunk;entitySections:[Lnet/minecraft/util/collection/TypeFilterableList;")
    @Definition(id = "add", method = "Lnet/minecraft/util/collection/TypeFilterableList;add(Ljava/lang/Object;)Z")
    @Expression("@(this.entitySections[?]).add(?)")
    @WrapOperation(
            method = "addEntity",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private TypeFilterableList<Entity> createEntitySection(TypeFilterableList<Entity>[] sections, int index, Operation<TypeFilterableList<Entity>> original) {
        TypeFilterableList<Entity> section = sections[index];
        if (section == EMPTY_SECTION) {
            sections[index] = section = new TypeFilterableList<>(Entity.class);
        }
        return section;
    }
}
