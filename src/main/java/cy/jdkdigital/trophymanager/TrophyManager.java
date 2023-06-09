package cy.jdkdigital.trophymanager;

import cy.jdkdigital.trophymanager.client.render.block.TrophyBlockEntityRenderer;
import cy.jdkdigital.trophymanager.common.block.TrophyBlock;
import cy.jdkdigital.trophymanager.compat.CuriosCompat;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
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
        MinecraftForge.EVENT_BUS.addListener(this::onAdvancementEarned);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::modComms);
        modEventBus.addListener(this::doCommonStuff);
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

    private void modComms(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.register();
        }
    }

    private void onEntityDeath(final LivingDeathEvent event) {
        Entity deadEntity = event.getEntity();
        Entity source = event.getSource().getEntity();
        if (TrophyManagerConfig.GENERAL.dropFromMobs.get() && !(deadEntity instanceof Player) && source instanceof ServerPlayer player && (!(source instanceof FakePlayer) || TrophyManagerConfig.GENERAL.allowFakePlayer.get())) {
            Double chance = deadEntity.canChangeDimensions() ? TrophyManagerConfig.GENERAL.dropChanceMobs.get() : TrophyManagerConfig.GENERAL.dropChanceBoss.get();

            boolean willDropTrophy = chance >= deadEntity.level.random.nextDouble();

            if (TrophyManagerConfig.GENERAL.applyLooting.get()) {
                // Each level of looting gives an extra roll
                int lootingLevel = player.getMainHandItem().getEnchantmentLevel(Enchantments.MOB_LOOTING);
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
        } else if (TrophyManagerConfig.GENERAL.dropFromPlayers.get() && deadEntity instanceof Player killedPlayer && source instanceof ServerPlayer player && (!(source instanceof FakePlayer) || TrophyManagerConfig.GENERAL.allowFakePlayer.get())) {
            Double chance = TrophyManagerConfig.GENERAL.dropChancePlayers.get();

            boolean willDropTrophy = chance >= deadEntity.level.random.nextDouble();

            if (TrophyManagerConfig.GENERAL.applyLooting.get()) {
                // Each level of looting gives an extra roll
                int lootingLevel = player.getMainHandItem().getEnchantmentLevel(Enchantments.MOB_LOOTING);
                for (int i = 0; i < (1 + lootingLevel); i++) {
                    willDropTrophy = willDropTrophy || chance >= deadEntity.level.random.nextDouble();
                }
            }

            if (willDropTrophy) {
                ItemStack trophy = TrophyBlock.createPlayerTrophy(killedPlayer);
                Block.popResource(deadEntity.level, deadEntity.blockPosition(), trophy);
            }
        }
    }

    private void onAdvancementEarned(final AdvancementEvent.AdvancementEarnEvent event) {
        if (ModList.get().isLoaded("the_bumblezone")) {
            ItemStack trophy = null;
            if (event.getAdvancement().getId().equals(new ResourceLocation("the_bumblezone", "the_bumblezone/the_queens_desire/journeys_end"))) {
                trophy = TrophyBlock.createTrophy("the_bumblezone:bee_queen", new CompoundTag(), "Queen Bee");
            } else if (event.getAdvancement().getId().equals(new ResourceLocation("the_bumblezone", "the_bumblezone/beehemoth/queen_beehemoth"))) {
                trophy = TrophyBlock.createTrophy("the_bumblezone:beehemoth", new CompoundTag(), "Beehemoth");
            }

            if (trophy != null && event.getEntity().addItem(trophy)) {
                event.getEntity().drop(trophy, false);
            }
        }
    }
}
