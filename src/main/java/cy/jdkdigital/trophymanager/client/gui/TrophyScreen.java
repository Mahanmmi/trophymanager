package cy.jdkdigital.trophymanager.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import cy.jdkdigital.trophymanager.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TrophyScreen extends Screen
{
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private static final ResourceLocation GUI = new ResourceLocation(TrophyManager.MODID, "textures/gui/trophy.png");
    private final TrophyBlockEntity trophy;

    protected TrophyScreen(BlockPos pos) {
        super(new TranslationTextComponent("gui.trophy.screen"));
        World level = TrophyManager.proxy.getWorld();
        trophy = (TrophyBlockEntity) level.getBlockEntity(pos);
    }

    @Override
    protected void init() {
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;

        addButton(new Button(relX + 10, relY + 10, 20, 20, new StringTextComponent("-"), button -> adjustScale(-1)));
        addButton(new Button(relX + 120, relY + 10, 20, 20, new StringTextComponent("+"), button -> adjustScale(1)));

        addButton(new Button(relX + 10, relY + 35, 20, 20, new StringTextComponent("-"), button -> adjustOffsetY(-1)));
        addButton(new Button(relX + 120, relY + 35, 20, 20, new StringTextComponent("+"), button -> adjustOffsetY(1)));

        addButton(new Button(relX + 10, relY + 60, 65, 20, new TranslationTextComponent("gui.cancel"), button -> close()));
        addButton(new Button(relX + 75, relY + 60, 65, 20, new TranslationTextComponent("gui.ok"), button -> save(this)));
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(GUI);

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
        trophy.scale = Math.round(trophy.scale * 10 + d) / 10f;
    }

    private void adjustOffsetY(double d) {
        if (Screen.hasShiftDown()) {
            d = d * 10;
        }
        trophy.offsetY = Math.round(trophy.offsetY * 10 + d) / 10d;
    }

    public static void open(BlockPos pos) {
        Minecraft.getInstance().setScreen(new TrophyScreen(pos));
    }

    public static void save(TrophyScreen screen) {
        CompoundNBT tag = new CompoundNBT();
        tag.putDouble("OffsetY", screen.trophy.offsetY);
        tag.putFloat("Scale", screen.trophy.scale);
        Networking.sendToServer(new Networking.PacketUpdateTrophy(screen.trophy.getBlockPos(), tag));
        close();
    }

    public static void close() {
        Minecraft.getInstance().setScreen(null);
    }
}
