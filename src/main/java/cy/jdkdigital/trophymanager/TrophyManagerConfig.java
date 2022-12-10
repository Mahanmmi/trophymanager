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
        public final ForgeConfigSpec.BooleanValue dropFromMobs;
        public final ForgeConfigSpec.BooleanValue applyLooting;
        public final ForgeConfigSpec.BooleanValue allowFakePlayer;
        public final ForgeConfigSpec.DoubleValue dropChanceBoss;
        public final ForgeConfigSpec.DoubleValue dropChanceMobs;
        public final ForgeConfigSpec.BooleanValue allowNonOpEdit;
        public final ForgeConfigSpec.DoubleValue maxSize;
        public final ForgeConfigSpec.ConfigValue<? extends String> defaultBaseBlock;
        public final ForgeConfigSpec.DoubleValue defaultYOffset;
        public final ForgeConfigSpec.ConfigValue<List<String>> nbtMap;
        public final ForgeConfigSpec.BooleanValue rotateItemTrophies;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            dropFromMobs = builder
                    .comment("Should trophies drop from mobs when killed by a player?")
                    .define("dropFromMobs", true);

            applyLooting = builder
                    .comment("Looting enchant will increase drop change")
                    .define("applyLooting", true);

            allowFakePlayer = builder
                    .comment("Allow fake players (machines) to get trophy drops")
                    .define("allowFakePlayer", true);

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

            defaultBaseBlock = builder
                    .comment("Block to use for trophies dropped when killing a mob.")
                    .define("defaultBaseBlock", "minecraft:smooth_stone_slab");

            defaultYOffset = builder
                    .comment("Default YOffset for trophies dropped when killing a mob. If defaultBaseBlock is a full block this should be 1.0, slabs 0.5 and carpets 0.1")
                    .defineInRange("defaultYOffset", 0.5, 0, Integer.MAX_VALUE);

            nbtMap = builder
                    .comment("List of entities which has NBT data that needs to be saved with the trophy. Format: modid:entityid:tag.")
                    .define("nbtMap", new ArrayList<>() {{
                        add("minecraft:axolotl:Variant");
                        add("minecraft:cat:Variant");
                        add("minecraft:cat:CollarColor");
                        add("minecraft:wolf:CollarColor");
                        add("minecraft:frog:variant");
                        add("minecraft:llama:Variant");
                        add("minecraft:horse:Variant");
                        add("minecraft:fox:Variant");
                        add("minecraft:parrot:Variant");
                        add("minecraft:panda:MainGene");
                        add("minecraft:panda:HiddenGene");
                        add("minecraft:mooshroom:Type");
                        add("minecraft:villager:VillagerData");
                        add("productivebees:configurable_bee:type");
                        add("infernalexp:shroomloin:ShroomloinType");
                        add("infernalexp:basalt_giant:Size");
                    }});

            rotateItemTrophies = builder
                    .comment("Have item on item trophies slowly rotate around.")
                    .define("rotateItemTrophies", true);

            builder.pop();
        }
    }
}