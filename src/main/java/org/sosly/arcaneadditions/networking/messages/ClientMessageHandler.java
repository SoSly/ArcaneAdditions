/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.networking.messages;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Logger;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.networking.BaseMessage;

public class ClientMessageHandler {
    public static <T extends BaseMessage> boolean validateBasics(T message, NetworkEvent.Context ctx) {
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);
        Logger logger;
        String messageName;
        if (sideReceived != LogicalSide.CLIENT) {
            logger = ArcaneAdditions.LOGGER;
            messageName = message.getClass().getName();
            logger.error(messageName + " received on wrong side: " + sideReceived);
            return false;
        } else if (!message.isMessageValid()) {
            logger = ArcaneAdditions.LOGGER;
            messageName = message.getClass().getName();
            logger.error(messageName + " was invalid: " + message);
            return false;
        } else {
            return true;
        }
    }
}
