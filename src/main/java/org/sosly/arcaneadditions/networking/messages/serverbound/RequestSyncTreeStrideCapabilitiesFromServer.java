/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.networking.messages.serverbound;

import com.mna.network.messages.BaseMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;
import org.sosly.arcaneadditions.networking.PacketHandler;
import org.sosly.arcaneadditions.networking.messages.ServerMessageHandler;
import org.sosly.arcaneadditions.networking.messages.clientbound.SyncTreeStrideCapabilitiesToClient;

import java.util.function.Supplier;

public class RequestSyncTreeStrideCapabilitiesFromServer extends BaseMessage {
    public static RequestSyncTreeStrideCapabilitiesFromServer decode(FriendlyByteBuf buf) {
        RequestSyncTreeStrideCapabilitiesFromServer msg = new RequestSyncTreeStrideCapabilitiesFromServer();
        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(RequestSyncTreeStrideCapabilitiesFromServer msg, FriendlyByteBuf buf) {}

    public static void handleRequestSyncTreeStrideCapabilities(RequestSyncTreeStrideCapabilitiesFromServer msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        if (ServerMessageHandler.validateBasics(msg, ctx)) {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                player.level.getCapability(TreestrideProvider.TREESTRIDE).ifPresent(cap -> {
                    PacketHandler.network.send(PacketDistributor.PLAYER.with(() -> player), new SyncTreeStrideCapabilitiesToClient(cap));
                });
            }
        }
    }
}
