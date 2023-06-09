package cy.jdkdigital.trophymanager;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class TrophyManagerConfig
{
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final General GENERAL = new General(SERVER_BUILDER);

    static {
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static class General
    {
        public final ForgeConfigSpec.BooleanValue dropFromPlayers;
        public final ForgeConfigSpec.BooleanValue dropFromMobs;
        public final ForgeConfigSpec.BooleanValue applyLooting;
        public final ForgeConfigSpec.BooleanValue allowFakePlayer;
        public final ForgeConfigSpec.DoubleValue dropChancePlayers;
        public final ForgeConfigSpec.DoubleValue dropChanceBoss;
        public final ForgeConfigSpec.DoubleValue dropChanceMobs;
        public final ForgeConfigSpec.BooleanValue allowNonOpEdit;
        public final ForgeConfigSpec.DoubleValue maxSize;
        public final ForgeConfigSpec.DoubleValue maxYOffset;
        public final ForgeConfigSpec.ConfigValue<? extends String> defaultBaseBlock;
        public final ForgeConfigSpec.DoubleValue defaultYOffset;
        public final ForgeConfigSpec.ConfigValue<String> nbtMap;
        public final ForgeConfigSpec.BooleanValue rotateItemTrophies;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            dropFromPlayers = builder
                    .comment("Should trophies drop from players when killed by another player?")
                    .define("dropFromPlayers", true);

            dropFromMobs = builder
                    .comment("Should trophies drop from mobs when killed by a player?")
                    .define("dropFromMobs", true);

            applyLooting = builder
                    .comment("Looting enchant will increase drop change")
                    .define("applyLooting", true);

            allowFakePlayer = builder
                    .comment("Allow fake players (machines) to get trophy drops")
                    .define("allowFakePlayer", true);

            dropChancePlayers = builder
                    .comment("Drop chance for trophies when a player is killed by a player.")
                    .defineInRange("dropChancePlayers", 1.0, 0, 1);

            dropChanceBoss = builder
                    .comment("Drop chance for trophies when a boss entity is killed by a player.")
                    .defineInRange("dropChanceBoss", 0.20, 0, 1);

            dropChanceMobs = builder
                    .comment("Drop chance for trophies when a normal entity is killed by a player.")
                    .defineInRange("dropChanceMobs", 0.02, 0, 1);

            allowNonOpEdit = builder
                    .comment("Allow non opped players to change the settings for a trophy.")
                    .define("allowNonOpEdit", true);

            maxSize = builder
                    .comment("Maximum size multiplier for a trophy.")
                    .defineInRange("maxSize", 20.00, 0, Integer.MAX_VALUE);

            maxYOffset = builder
                    .comment("Maximum Y offset for a trophy.")
                    .defineInRange("maxYOffset", 2000.00, 0, Integer.MAX_VALUE);

            defaultBaseBlock = builder
                    .comment("Block to use for trophies dropped when killing a mob.")
                    .define("defaultBaseBlock", "minecraft:smooth_stone_slab");

            defaultYOffset = builder
                    .comment("Default YOffset for trophies dropped when killing a mob. If defaultBaseBlock is a full block this should be 1.0, slabs 0.5 and carpets 0.1")
                    .defineInRange("defaultYOffset", 0.5, 0, Integer.MAX_VALUE);

            nbtMap = builder
                    .comment("Comma separated list of entities which has NBT data that needs to be saved with the trophy. Format: modid:entityid:tag.")
                    .define("nbtMap", "minecraft:axolotl:Variant,minecraft:cat:Variant,minecraft:cat:CollarColor,minecraft:wolf:CollarColor,minecraft:frog:variant,minecraft:llama:Variant,minecraft:horse:Variant,minecraft:fox:Variant,minecraft:parrot:Variant,minecraft:panda:MainGene,minecraft:panda:HiddenGene,minecraft:mooshroom:Type,minecraft:villager:VillagerData,productivebees:configurable_bee:type,infernalexp:shroomloin:ShroomloinType,infernalexp:basalt_giant:Size");

            rotateItemTrophies = builder
                    .comment("Have items on item trophies slowly rotate around.")
                    .define("rotateItemTrophies", true);

            builder.pop();
        }
    }
}