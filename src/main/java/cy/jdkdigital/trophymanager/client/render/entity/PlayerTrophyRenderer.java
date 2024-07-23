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
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTrophyRenderer extends AbstractZombieRenderer<RenderPlayer, ZombieModel<RenderPlayer>>
{
    static Map<UUID, PlayerInfo> playerInfoCache = new HashMap<>();

    public PlayerTrophyRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public PlayerTrophyRenderer(EntityRendererProvider.Context context, ModelLayerLocation p_174459_, ModelLayerLocation p_174460_, ModelLayerLocation p_174461_) {
        super(context, new ZombieModel<>(context.bakeLayer(p_174459_)), new ZombieModel<>(context.bakeLayer(p_174460_)), new ZombieModel<>(context.bakeLayer(p_174461_)));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(RenderPlayer player) {
        if (!player.getUUIDData().isEmpty()) {
            return getSkinTextureLocation(UUID.fromString(player.getUUIDData()));
        }
        return DefaultPlayerSkin.getDefaultSkin();
    }

    @Nullable
    protected static PlayerInfo getPlayerInfo(UUID uuid) {
        if (!playerInfoCache.containsKey(uuid)) {
            playerInfoCache.put(uuid, Minecraft.getInstance().getConnection().getPlayerInfo(uuid));
        }
        return playerInfoCache.get(uuid);
    }

    public static boolean isSkinLoaded(UUID uuid) {
        PlayerInfo playerinfo = getPlayerInfo(uuid);
        return playerinfo != null && playerinfo.isSkinLoaded();
    }

    public static ResourceLocation getSkinTextureLocation(UUID uuid) {
        PlayerInfo playerinfo = getPlayerInfo(uuid);
        TrophyManager.LOGGER.info("playerInfo " + uuid + "/"  +playerinfo);
        if (playerinfo != null) {
            TrophyManager.LOGGER.info("playerInfo " + playerinfo.getTabListDisplayName());
        }
        return playerinfo == null ? DefaultPlayerSkin.getDefaultSkin(uuid) : playerinfo.getSkinLocation();
    }
}
