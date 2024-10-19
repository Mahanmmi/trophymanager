package cy.jdkdigital.trophymanager.client.render.entity;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.entity.RenderPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTrophyRenderer extends AbstractZombieRenderer<RenderPlayer, ZombieModel<RenderPlayer>>
{
    static Map<UUID, PlayerSkin> playerInfoCache = new HashMap<>();

    public PlayerTrophyRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public PlayerTrophyRenderer(EntityRendererProvider.Context context, ModelLayerLocation p_174459_, ModelLayerLocation p_174460_, ModelLayerLocation p_174461_) {
        super(context, new ZombieModel<>(context.bakeLayer(p_174459_)), new ZombieModel<>(context.bakeLayer(p_174460_)), new ZombieModel<>(context.bakeLayer(p_174461_)));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(RenderPlayer player) {
        if (!player.getUUIDData().isEmpty()) {
//            return getSkinTextureLocation(UUID.fromString(player.getUUIDData()));
        }
        return DefaultPlayerSkin.getDefaultTexture();
    }

    @Nullable
    protected static PlayerSkin getPlayerInfo(ResolvableProfile pProfile) {
        if (!playerInfoCache.containsKey(pProfile.id().get())) {
            SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
            playerInfoCache.put(pProfile.id().get(), skinmanager.getInsecureSkin(pProfile.gameProfile()));
        }
        return playerInfoCache.get(pProfile.id().get());
    }

    public static boolean isSkinLoaded(ResolvableProfile pProfile) {
        PlayerSkin playerinfo = getPlayerInfo(pProfile);
        return playerinfo != null;
    }

//    public static ResourceLocation getSkinTextureLocation(ResolvableProfile pProfile) {
//        PlayerSkin playerinfo = getPlayerInfo(pProfile);
//        if (playerinfo != null) {
//            TrophyManager.LOGGER.info("playerInfo " + playerinfo.texture());
//        }
//        return playerinfo == null ? DefaultPlayerSkin.getDefaultTexture() : playerinfo.getSkin().texture();
//    }
}
