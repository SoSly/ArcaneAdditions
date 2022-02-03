package org.sosly.arcaneadditions.networking.messages.clientbound;

import com.mna.network.messages.BaseMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.polymorph.IPolymorphCapability;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphCapability;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.networking.messages.ClientMessageHandler;

import java.util.Optional;
import java.util.function.Supplier;

public class SyncPolymorphCapabilitiesToClient extends BaseMessage {
    private IPolymorphCapability cap;

    public SyncPolymorphCapabilitiesToClient() {
        this.cap = new PolymorphCapability();
    }

    public SyncPolymorphCapabilitiesToClient(IPolymorphCapability cap) {
        this.cap = cap;
    }

    public static SyncPolymorphCapabilitiesToClient decode(FriendlyByteBuf buf) {
        SyncPolymorphCapabilitiesToClient msg = new SyncPolymorphCapabilitiesToClient();

        try {
            msg.cap.setCasterUUID(buf.readUUID());
            msg.cap.setComplexity(buf.readFloat());
            msg.cap.setHealth(buf.readFloat());
        } catch (IndexOutOfBoundsException | IllegalArgumentException err) {
            ArcaneAdditions.LOGGER.error("Exception while reading SyncPolymorphCapabilitiesToClient: {}", err.toString());
            return msg;
        }

        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(SyncPolymorphCapabilitiesToClient msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.cap.getCasterUUID());
        buf.writeFloat(msg.cap.getComplexity());
        buf.writeFloat(msg.cap.getHealth());
    }

    public static void handlePolymorphCapabilitiesSync(SyncPolymorphCapabilitiesToClient msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        if (ClientMessageHandler.validateBasics(msg, ctx)) {
            LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
            Optional<Level> level = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
            if (level.isEmpty()) {
                ArcaneAdditions.LOGGER.error("SyncPolymorphCapabilitiesToClient context could not provide a ClientWorld");
            } else {
                ctx.enqueueWork(() -> {
                    Player player = ArcaneAdditions.instance.proxy.getClientPlayer();
                    if (player != null) {
                        player.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
                            polymorph.setCasterUUID(msg.cap.getCasterUUID());
                            polymorph.setComplexity(msg.cap.getComplexity());
                            polymorph.setHealth(msg.cap.getHealth());
                        });
                    }
                });
            }
        }
    }
}
