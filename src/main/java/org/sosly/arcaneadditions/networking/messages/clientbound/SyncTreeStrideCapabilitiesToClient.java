/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.networking.messages.clientbound;

import com.mna.network.messages.BaseMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import org.sosly.arcaneadditions.capabilities.treestride.ITreestrideCapability;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideCapability;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;
import org.sosly.arcaneadditions.networking.messages.ClientMessageHandler;
import org.sosly.arcaneadditions.spells.components.TreeStrideComponent;

import java.util.Optional;
import java.util.function.Supplier;

public class SyncTreeStrideCapabilitiesToClient extends BaseMessage {
    private final ITreestrideCapability cap;

    public SyncTreeStrideCapabilitiesToClient(ITreestrideCapability cap) {
        this.cap = cap;
    }

    public static SyncTreeStrideCapabilitiesToClient decode(FriendlyByteBuf buf) {
        SyncTreeStrideCapabilitiesToClient msg;

        try {
            CompoundTag nbt = buf.readNbt();
            TreestrideProvider provider = new TreestrideProvider();
            provider.deserializeNBT(nbt);
            msg = new SyncTreeStrideCapabilitiesToClient(provider.getCapability(TreestrideProvider.TREESTRIDE).orElse(null));
        } catch (IndexOutOfBoundsException | IllegalArgumentException err) {
            ArcaneAdditions.LOGGER.error("Exception while reading SyncPolymorphCapabilitiesToClient: {}", err.toString());
            return null;
        }

        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(SyncTreeStrideCapabilitiesToClient msg, FriendlyByteBuf buf) {
        CompoundTag nbt = (CompoundTag)TreestrideProvider.serializeNBT(msg.cap);
        buf.writeNbt(nbt);
    }

    public static void handleTreeStrideCapabilitiesSync(SyncTreeStrideCapabilitiesToClient msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        if (ClientMessageHandler.validateBasics(msg, ctx)) {
            LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
            Optional<Level> level = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
            if (level.isEmpty()) {
                ArcaneAdditions.LOGGER.error("SyncPolymorphCapabilitiesToClient context could not provide a ClientWorld");
            } else {
                level.get().getCapability(TreestrideProvider.TREESTRIDE).ifPresent(treestride -> {
                    treestride.reset();
                    msg.cap.getAllDestinations().forEach((uuid, destinations) -> {
                        destinations.forEach((pos, name) -> {
                            treestride.addDestination(uuid, name, pos);
                        });
                    });
                });
            }
        }
    }
}
