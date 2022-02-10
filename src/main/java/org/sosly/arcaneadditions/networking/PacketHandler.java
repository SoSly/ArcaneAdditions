/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.networking;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.networking.messages.clientbound.SyncPolymorphCapabilitiesToClient;
import org.sosly.arcaneadditions.networking.messages.clientbound.SyncTreeStrideCapabilitiesToClient;
import org.sosly.arcaneadditions.networking.messages.serverbound.RemoveTreeStrideDestination;
import org.sosly.arcaneadditions.networking.messages.serverbound.RequestSyncTreeStrideCapabilitiesFromServer;
import org.sosly.arcaneadditions.networking.messages.serverbound.NewTreeStrideDestination;
import org.sosly.arcaneadditions.networking.messages.serverbound.TreeStridePlayer;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod.EventBusSubscriber(
        modid = ArcaneAdditions.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel network = NetworkRegistry.newSimpleChannel(RLoc.create("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        int packet_id = 1;
        network.registerMessage(packet_id++, NewTreeStrideDestination.class, NewTreeStrideDestination::encode, NewTreeStrideDestination::decode, NewTreeStrideDestination::handleNewTreeStrideDestination);
        network.registerMessage(packet_id++, RemoveTreeStrideDestination.class, RemoveTreeStrideDestination::encode, RemoveTreeStrideDestination::decode, RemoveTreeStrideDestination::handleRemoveTreeStrideDestination);
        network.registerMessage(packet_id++, RequestSyncTreeStrideCapabilitiesFromServer.class, RequestSyncTreeStrideCapabilitiesFromServer::encode, RequestSyncTreeStrideCapabilitiesFromServer::decode, RequestSyncTreeStrideCapabilitiesFromServer::handleRequestSyncTreeStrideCapabilities);
        network.registerMessage(packet_id++, SyncPolymorphCapabilitiesToClient.class, SyncPolymorphCapabilitiesToClient::encode, SyncPolymorphCapabilitiesToClient::decode, SyncPolymorphCapabilitiesToClient::handlePolymorphCapabilitiesSync);
        network.registerMessage(packet_id++, SyncTreeStrideCapabilitiesToClient.class, SyncTreeStrideCapabilitiesToClient::encode, SyncTreeStrideCapabilitiesToClient::decode, SyncTreeStrideCapabilitiesToClient::handleTreeStrideCapabilitiesSync);
        network.registerMessage(packet_id++, TreeStridePlayer.class, TreeStridePlayer::encode, TreeStridePlayer::decode, TreeStridePlayer::handleTreeStridePlayer);
        ArcaneAdditions.LOGGER.info("Arcane Additions registered {} network messages", packet_id);
    }
}
