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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;
import org.sosly.arcaneadditions.networking.messages.ServerMessageHandler;

import java.util.function.Supplier;

public class NewTreeStrideDestination extends BaseMessage {
    private final Component name;

    public NewTreeStrideDestination(Component name) {
        this.name = name;
    }

    public static NewTreeStrideDestination decode(FriendlyByteBuf buf) {
        NewTreeStrideDestination msg;

        try {
            Component name = buf.readComponent();
            msg = new NewTreeStrideDestination(name);
        } catch (IndexOutOfBoundsException | IllegalArgumentException err) {
            ArcaneAdditions.LOGGER.error("Exception while reading SendNewTreeStrideDestinationToServer: {}", err.toString());
            return null;
        }

        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(NewTreeStrideDestination msg, FriendlyByteBuf buf) {
        buf.writeComponent(msg.name);
    }

    public static void handleNewTreeStrideDestination(NewTreeStrideDestination msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        if (ServerMessageHandler.validateBasics(msg, ctx)) {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                Level level = player.getLevel();

                ctx.enqueueWork(() -> {
                    level.getCapability(TreestrideProvider.TREESTRIDE).ifPresent(treestride -> {
                        BlockPos pos = treestride.getCurrentPosition(player);
                        if (pos != null) {
                            treestride.addDestination(player, msg.name.getContents(), pos);
                            treestride.clearCurrentPosition(player);
                        }
                    });
                });
            }
        }
    }
}
