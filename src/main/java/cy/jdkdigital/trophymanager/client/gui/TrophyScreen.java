package cy.jdkdigital.trophymanager.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.TrophyManagerConfig;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import cy.jdkdigital.trophymanager.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class TrophyScreen extends Screen
{
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private static final ResourceLocation GUI = new ResourceLocation(TrophyManager.MODID, "textures/gui/trophy.png");
    private final TrophyBlockEntity trophy;

    protected TrophyScreen(BlockPos pos) {
        super(Component.translatable("gui.trophy.screen"));
        Level level = TrophyManager.proxy.getWorld();
        trophy = (TrophyBlockEntity) level.getBlockEntity(pos);
    }

    @Override
    protected void init() {
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;

        addRenderableWidget(Button.builder(Component.literal("-"), button -> adjustScale(-1)).pos(relX + 10, relY + 10).size(20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+"), button -> adjustScale(1)).pos(relX + 120, relY + 10).size(20, 20).build());

        addRenderableWidget(Button.builder(Component.literal("-"), button -> adjustOffsetY(-1)).pos(relX + 10, relY + 35).size(20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+"), button -> adjustOffsetY(1)).pos(relX + 120, relY + 35).size(20, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> close()).pos(relX + 10, relY + 60).size(65, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ok"), button -> save(this)).pos(relX + 76, relY + 60).size(65, 20).build());
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);

        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, WIDTH, HEIGHT);

        drawCenteredString(matrixStack, font, "" + trophy.scale, relX + 75, relY + 15, 10526880);
        drawCenteredString(matrixStack, font, "" + trophy.offsetY, relX + 75, relY + 40, 10526880);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void adjustScale(float d) {
        if (Screen.hasShiftDown()) {
            d = d * 10;
        }
        trophy.scale = (float) Math.min(Math.round(trophy.scale * 10 + d) / 10f, TrophyManagerConfig.GENERAL.maxSize.get());
    }

    private void adjustOffsetY(double d) {
        if (Screen.hasShiftDown()) {
            d = d * 10;
        }
        trophy.offsetY = Math.min(Math.round(trophy.offsetY * 10 + d) / 10d, TrophyManagerConfig.GENERAL.maxYOffset.get());
    }

    public static void open(BlockPos pos) {
        Minecraft.getInstance().setScreen(new TrophyScreen(pos));
    }

    public static void save(TrophyScreen screen) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("OffsetY", screen.trophy.offsetY);
        tag.putFloat("Scale", screen.trophy.scale);
        Networking.sendToServer(new Networking.PacketUpdateTrophy(screen.trophy.getBlockPos(), tag));
        close();
    }

    public static void close() {
        Minecraft.getInstance().setScreen(null);
    }
}
