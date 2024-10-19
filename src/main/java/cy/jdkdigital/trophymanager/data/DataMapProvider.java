package cy.jdkdigital.trophymanager.data;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.datamap.NbtMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataMapProvider extends net.neoforged.neoforge.common.data.DataMapProvider
{
    protected DataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        final var nbt = builder(TrophyManager.NBT_MAP);

        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.AXOLOTL), new NbtMap(List.of("Variant")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.CAT), new NbtMap(List.of("variant", "CollarColor")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WOLF), new NbtMap(List.of("variant", "CollarColor")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.FROG), new NbtMap(List.of("variant")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.LLAMA), new NbtMap(List.of("Variant")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.HORSE), new NbtMap(List.of("Variant")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.FOX), new NbtMap(List.of("Variant")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PARROT), new NbtMap(List.of("Variant")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PANDA), new NbtMap(List.of("MainGene", "HiddenGene")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.MOOSHROOM), new NbtMap(List.of("Type")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.VILLAGER), new NbtMap(List.of("VillagerData")), false);
        nbt.add(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE_VILLAGER), new NbtMap(List.of("VillagerData")), false);
        nbt.add(ResourceLocation.parse("productivebees:configurable_bee"), new NbtMap(List.of("type")), false, new ModLoadedCondition("productivebeees"));
        nbt.add(ResourceLocation.parse("infernalexp:shroomloin"), new NbtMap(List.of("ShroomloinType")), false, new ModLoadedCondition("infernalexp"));
        nbt.add(ResourceLocation.parse("infernalexp:basalt_giant"), new NbtMap(List.of("Size")), false, new ModLoadedCondition("infernalexp"));
    }
}
