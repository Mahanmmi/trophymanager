package cy.jdkdigital.trophymanager.network;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.client.gui.TrophyScreen;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class Networking
{
    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TrophyManager.MODID, "trophy"),
                () -> "1.0",
                s -> true,
                s -> true);

        INSTANCE.messageBuilder(PacketOpenGui.class, nextID())
                .encoder((packetOpenGui, packetBuffer) -> {packetBuffer.writeBlockPos(packetOpenGui.pos);})
                .decoder(buf -> new PacketOpenGui(buf.readBlockPos()))
                .consumer(PacketOpenGui::handle)
                .add();
        INSTANCE.messageBuilder(PacketUpdateTrophy.class, nextID())
                .encoder((packetUpdateTrophy, packetBuffer) -> {
                    packetBuffer.writeBlockPos(packetUpdateTrophy.pos);
                    packetBuffer.writeNbt(packetUpdateTrophy.tag);
                })
                .decoder(buf -> new PacketUpdateTrophy(buf.readBlockPos(), buf.readAnySizeNbt()))
                .consumer(PacketUpdateTrophy::handle)
                .add();
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static class PacketUpdateTrophy {
        private final BlockPos pos;
        private final CompoundNBT tag;

        public PacketUpdateTrophy(BlockPos pos, CompoundNBT tag) {
            this.pos = pos;
            this.tag = tag;
        }

        public boolean handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    World level = player.getLevel();
                    TileEntity te = level.getBlockEntity(pos);
                    if (te instanceof TrophyBlockEntity) {
                        ((TrophyBlockEntity) te).offsetY = tag.getDouble("OffsetY");
                        ((TrophyBlockEntity) te).scale = tag.getFloat("Scale");
                        te.setChanged();
                    }
                }
            });
            return true;
        }
    }

    public static class PacketOpenGui {
        private BlockPos pos;

        public PacketOpenGui(BlockPos pos) {
            this.pos = pos;
        }

        public boolean handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> TrophyScreen.open(pos));
            return true;
        }
    }
}