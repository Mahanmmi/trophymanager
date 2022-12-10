package cy.jdkdigital.trophymanager;

import cy.jdkdigital.trophymanager.client.render.block.TrophyBlockEntityRenderer;
import cy.jdkdigital.trophymanager.common.block.TrophyBlock;
import cy.jdkdigital.trophymanager.init.ModBlockEntities;
import cy.jdkdigital.trophymanager.init.ModBlocks;
import cy.jdkdigital.trophymanager.network.Networking;
import cy.jdkdigital.trophymanager.setup.ClientProxy;
import cy.jdkdigital.trophymanager.setup.IProxy;
import cy.jdkdigital.trophymanager.setup.ServerProxy;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
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
        modEventBus.addListener(this::tabs);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TrophyManagerConfig.SERVER_CONFIG);
    }

    private void doCommonStuff(final FMLCommonSetupEvent event) {
        Networking.registerMessages();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(ModBlockEntities.TROPHY.get(), TrophyBlockEntityRenderer::new);
    }

    private void onEntityDeath(final LivingDeathEvent event) {
        Entity deadEntity = event.getEntity();
        Entity source = event.getSource().getEntity();
        if (TrophyManagerConfig.GENERAL.dropFromMobs.get() && !(deadEntity instanceof Player) && source instanceof ServerPlayer player && (!(source instanceof FakePlayer) || TrophyManagerConfig.GENERAL.allowFakePlayer.get())) {
            Double chance = deadEntity.canChangeDimensions() ? TrophyManagerConfig.GENERAL.dropChanceMobs.get() : TrophyManagerConfig.GENERAL.dropChanceBoss.get();

            boolean willDropTrophy = chance >= deadEntity.level.random.nextDouble();

            if (TrophyManagerConfig.GENERAL.applyLooting.get()) {
                // Each level of looting gives an extra roll
                int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, player.getMainHandItem());
                for (int i = 0; i < (1 + lootingLevel); i++) {
                    willDropTrophy = willDropTrophy || chance >= deadEntity.level.random.nextDouble();
                }
            }

            if (willDropTrophy) {
                CompoundTag entityTag = new CompoundTag();
                deadEntity.saveWithoutId(entityTag);
                ItemStack trophy = TrophyBlock.createTrophy(deadEntity, entityTag);
                Block.popResource(deadEntity.level, deadEntity.blockPosition(), trophy);
            }
        }
    }

    private void tabs(final CreativeModeTabEvent.BuildContents event) {
        if (event.getTab().equals(CreativeModeTabs.OP_BLOCKS)) {
            String[] entities = {"allay", "axolotl", "bat", "bee", "blaze", "camel", "cat", "cave_spider", "chicken", "cow", "creeper", "dolphin", "donkey", "drowned", "elder_guardian", "ender_dragon", "enderman", "endermite", "evoker", "fox", "frog", "ghast", "glow_squid", "goat", "guardian", "hoglin", "horse", "husk", "illusioner", "iron_golem", "llama", "magma_cube", "mule", "mooshroom", "ocelot", "panda", "parrot", "phantom", "pig", "piglin", "piglin_brute", "pillager", "polar_bear", "pufferfish", "rabbit", "ravager", "sheep", "shulker", "silverfish", "skeleton", "skeleton_horse", "slime", "snow_golem", "spider", "squid", "stray", "strider", "tadpole", "trader_llama", "tropical_fish", "turtle", "vex", "villager", "vindicator", "wandering_trader", "warden", "witch", "wither", "wither_skeleton", "wolf", "zoglin", "zombie", "zombie_horse", "zombie_villager", "zombified_piglin"};

            event.register(event.getTab(), (enabledFlags, populator, hasPermissions) -> {
                for (String entityId : entities) {
                    populator.accept(TrophyBlock.createTrophy("minecraft:" + entityId, new CompoundTag(), idToName("minecraft:" + entityId)));
                }
            });
        }
    }

    private static String idToName(String id) {
        int start = id.indexOf(":") + 1;
        return id.substring(start, start + 1).toUpperCase() + id.substring(start + 1).replace("_", " ");
    }
}
