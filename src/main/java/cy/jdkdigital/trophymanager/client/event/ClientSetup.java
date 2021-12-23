package cy.jdkdigital.trophymanager.client.event;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.client.render.block.TrophyBlockEntityRenderer;
import cy.jdkdigital.trophymanager.client.render.entity.TrophyMinecartRenderer;
import cy.jdkdigital.trophymanager.init.ModBlockEntities;
import cy.jdkdigital.trophymanager.init.ModEntities;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TrophyManager.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup
{
    public static void init(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(ModBlockEntities.TROPHY.get(), TrophyBlockEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.TROPHY_MINECART.get(), TrophyMinecartRenderer::new);
    }
}
