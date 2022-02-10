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
import net.minecraftforge.network.NetworkEvent;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.networking.messages.ServerMessageHandler;

import java.util.function.Supplier;

public class TreeStridePlayer extends BaseMessage {
    BlockPos pos;

    public TreeStridePlayer() {}

    public TreeStridePlayer(BlockPos pos) {
        this.pos = pos;
    }

    public static TreeStridePlayer decode(FriendlyByteBuf buf) {
        TreeStridePlayer msg = new TreeStridePlayer();
        try {
            msg.pos = buf.readBlockPos();
        } catch (IndexOutOfBoundsException | IllegalArgumentException err) {
            ArcaneAdditions.LOGGER.error("Exception while reading SendTreeStridePlayerToServer: {}", err.toString());
            return null;
        }

        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(TreeStridePlayer msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static void handleTreeStridePlayer(TreeStridePlayer msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        if (ServerMessageHandler.validateBasics(msg, ctx)) {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                ctx.enqueueWork(() -> {
                    player.teleportTo(msg.pos.getX(), msg.pos.getY(), msg.pos.getZ());
                });
            }
        }
    }
}
