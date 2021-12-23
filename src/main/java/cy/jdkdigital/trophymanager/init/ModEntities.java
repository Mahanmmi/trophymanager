package cy.jdkdigital.trophymanager.init;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.entity.TrophyMinecartEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = TrophyManager.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, TrophyManager.MODID);

    public static RegistryObject<EntityType<TrophyMinecartEntity>> TROPHY_MINECART = ENTITIES.register("trophy_minecart", () -> EntityType.Builder.<TrophyMinecartEntity>of(TrophyMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).build(TrophyManager.MODID + ":trophy_minecart"));
}
