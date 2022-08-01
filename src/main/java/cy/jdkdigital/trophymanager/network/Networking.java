package cy.jdkdigital.trophymanager.network;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.TrophyManagerConfig;
import cy.jdkdigital.trophymanager.client.gui.TrophyScreen;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

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
                .consumerNetworkThread(PacketOpenGui::handle)
                .add();
        INSTANCE.messageBuilder(PacketUpdateTrophy.class, nextID())
                .encoder((packetUpdateTrophy, packetBuffer) -> {
                    packetBuffer.writeBlockPos(packetUpdateTrophy.pos);
                    packetBuffer.writeNbt(packetUpdateTrophy.tag);
                })
                .decoder(buf -> new PacketUpdateTrophy(buf.readBlockPos(), buf.readAnySizeNbt()))
                .consumerMainThread(PacketUpdateTrophy::handle)
                .add();
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static class PacketUpdateTrophy {
        private final BlockPos pos;
        private final CompoundTag tag;

        public PacketUpdateTrophy(BlockPos pos, CompoundTag tag) {
            this.pos = pos;
            this.tag = tag;
        }

        public boolean handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    ServerLevel level = player.getLevel();
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TrophyBlockEntity trophyBlockEntity) {
                        trophyBlockEntity.offsetY = tag.getDouble("OffsetY");
                        trophyBlockEntity.scale = (float) Math.min(tag.getFloat("Scale"), TrophyManagerConfig.GENERAL.maxSize.get());
                        blockEntity.setChanged();
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