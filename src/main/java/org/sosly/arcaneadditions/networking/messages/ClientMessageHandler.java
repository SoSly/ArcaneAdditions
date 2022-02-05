package org.sosly.arcaneadditions.networking.messages;

import com.mna.network.messages.BaseMessage;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Logger;
import org.sosly.arcaneadditions.ArcaneAdditions;

public class ClientMessageHandler {
    public static <T extends BaseMessage> boolean validateBasics(T message, NetworkEvent.Context ctx) {
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);
        Logger var10000;
        String var10001;
        if (sideReceived != LogicalSide.CLIENT) {
            var10000 = ArcaneAdditions.LOGGER;
            var10001 = message.getClass().getName();
            var10000.error(var10001 + " received on wrong side: " + sideReceived);
            return false;
        } else if (!message.isMessageValid()) {
            var10000 = ArcaneAdditions.LOGGER;
            var10001 = message.getClass().getName();
            var10000.error(var10001 + " was invalid: " + message);
            return false;
        } else {
            return true;
        }
    }
}