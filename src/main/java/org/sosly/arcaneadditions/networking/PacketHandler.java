package org.sosly.arcaneadditions.networking;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.networking.messages.clientbound.SyncPolymorphCapabilitiesToClient;
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
        network.registerMessage(packet_id++, SyncPolymorphCapabilitiesToClient.class, SyncPolymorphCapabilitiesToClient::encode, SyncPolymorphCapabilitiesToClient::decode, SyncPolymorphCapabilitiesToClient::handlePolymorphCapabilitiesSync);
        ArcaneAdditions.LOGGER.info("Arcane Additions registered {} network messages", packet_id);
    }
}
