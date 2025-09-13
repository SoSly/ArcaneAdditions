/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.networking.messages.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.networking.BaseMessage;
import org.sosly.arcaneadditions.networking.messages.ServerMessageHandler;
import org.sosly.arcaneadditions.networking.messages.clientbound.UpdateFamiliarData;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.function.Supplier;

public class RequestFamiliarDataUpdate extends BaseMessage {
    public static RequestFamiliarDataUpdate decode(FriendlyByteBuf buf) {
        RequestFamiliarDataUpdate msg = new RequestFamiliarDataUpdate();
        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(RequestFamiliarDataUpdate msg, FriendlyByteBuf buf) {}

    public static void handleRequestFamiliarDataUpdate(RequestFamiliarDataUpdate msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        if (ServerMessageHandler.validateBasics(msg, ctx)) {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
                if (cap != null && cap.getFamiliar() != null) {
                    UpdateFamiliarData updatePacket = new UpdateFamiliarData(
                        cap.getFamiliar().getHealth(),
                        cap.getFamiliar().getMaxHealth(),
                        cap.getCastingResource().getAmount(),
                        cap.getCastingResource().getMaxAmount()
                    );
                    org.sosly.arcaneadditions.networking.PacketHandler.network.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player), 
                        updatePacket
                    );
                }
            }
        }
    }
}