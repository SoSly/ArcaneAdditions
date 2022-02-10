/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.networking.messages.serverbound;

import com.mna.network.messages.BaseMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;
import org.sosly.arcaneadditions.networking.messages.ServerMessageHandler;

import java.util.function.Supplier;

public class RemoveTreeStrideDestination extends BaseMessage {
    private final BlockPos pos;

    public RemoveTreeStrideDestination(BlockPos pos) {
        this.pos = pos;
    }

    public static RemoveTreeStrideDestination decode(FriendlyByteBuf buf) {
        RemoveTreeStrideDestination msg;

        try {
            BlockPos pos = buf.readBlockPos();
            msg = new RemoveTreeStrideDestination(pos);
        } catch (IndexOutOfBoundsException | IllegalArgumentException err) {
            ArcaneAdditions.LOGGER.error("Exception while reading SendNewTreeStrideDestinationToServer: {}", err.toString());
            return null;
        }

        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(RemoveTreeStrideDestination msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static void handleRemoveTreeStrideDestination(RemoveTreeStrideDestination msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        if (ServerMessageHandler.validateBasics(msg, ctx)) {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                Level level = player.getLevel();

                ctx.enqueueWork(() -> {
                    level.getCapability(TreestrideProvider.TREESTRIDE).ifPresent(treestride -> {
                        treestride.removeDestination(player, msg.pos);
                        treestride.clearCurrentPosition(player);
                    });
                });
            }
        }
    }
}
