package cy.jdkdigital.trophymanager;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

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
        public final ForgeConfigSpec.DoubleValue dropChanceBoss;
        public final ForgeConfigSpec.DoubleValue dropChanceMobs;
        public final ForgeConfigSpec.BooleanValue allowNonOpEdit;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            dropFromMobs = builder
                    .comment("Should trophies drop from mobs when killed by a player?")
                    .define("dropFromMobs", true);

            dropChanceBoss = builder
                    .comment("Drop chance for trophies when a boss entity is killed by a player.")
                    .defineInRange("dropChanceBoss", 0.20, 0, 1);

            dropChanceMobs = builder
                    .comment("Drop chance for trophies when a normal entity is killed by a player.")
                    .defineInRange("dropChanceMobs", 0.02, 0, 1);

            allowNonOpEdit = builder
                    .comment("Allow non opped players to change the settings for a trophy.")
                    .define("allowNonOpEdit", true);

            builder.pop();
        }
    }
}