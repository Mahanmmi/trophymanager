package cy.jdkdigital.trophymanager;

import cy.jdkdigital.trophymanager.client.render.block.TrophyBlockEntityRenderer;
import cy.jdkdigital.trophymanager.common.block.TrophyBlock;
import cy.jdkdigital.trophymanager.init.ModBlockEntities;
import cy.jdkdigital.trophymanager.init.ModBlocks;
import cy.jdkdigital.trophymanager.network.Networking;
import cy.jdkdigital.trophymanager.setup.ClientProxy;
import cy.jdkdigital.trophymanager.setup.IProxy;
import cy.jdkdigital.trophymanager.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("trophymanager")
public class TrophyManager
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "trophymanager";

    public static final IProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    //data get entity @s SelectedItem

    public TrophyManager() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.addListener(this::onEntityDeath);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::doCommonStuff);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TrophyManagerConfig.SERVER_CONFIG);
    }

    private void doCommonStuff(final FMLCommonSetupEvent event) {
        Networking.registerMessages();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(ModBlockEntities.TROPHY.get(), TrophyBlockEntityRenderer::new);
    }

    private void onEntityDeath(final LivingDeathEvent event) {
        Entity deadEntity = event.getEntity();
        Entity source = event.getSource().getEntity();
        if (TrophyManagerConfig.GENERAL.dropFromMobs.get() && !(deadEntity instanceof PlayerEntity) && source instanceof PlayerEntity) {
            World level = deadEntity.level;
            Double chance = deadEntity.canChangeDimensions() ? TrophyManagerConfig.GENERAL.dropChanceMobs.get() : TrophyManagerConfig.GENERAL.dropChanceBoss.get();

            boolean willDropTrophy = chance >= level.random.nextDouble();

            // Each level of looting gives an extra roll
            if ((source instanceof ServerPlayerEntity)) {
                ServerPlayerEntity player = (ServerPlayerEntity) source;
                int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, player.getMainHandItem());
                for (int i = 0; i < (1 + lootingLevel); i++) {
                    willDropTrophy = willDropTrophy || chance >= level.random.nextDouble();
                }
            }

            if (willDropTrophy) {
                CompoundNBT entityTag = new CompoundNBT();
                deadEntity.save(entityTag);
                ItemStack trophy = TrophyBlock.createTrophy(deadEntity, entityTag);
                Block.popResource(deadEntity.level, deadEntity.blockPosition(), trophy);
            }
        }
    }
}
