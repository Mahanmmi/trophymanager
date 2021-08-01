package cy.jdkdigital.trophymanager.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy
{
    World getWorld();

    PlayerEntity getPlayer();
}