package cy.jdkdigital.trophymanager.init;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.entity.RenderPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TrophyManager.MODID);

    public static final RegistryObject<EntityType<RenderPlayer>> PLAYER = ENTITIES.register("player", () -> EntityType.Builder.of(RenderPlayer::new, MobCategory.MISC).build(TrophyManager.MODID + ":player"));
}
