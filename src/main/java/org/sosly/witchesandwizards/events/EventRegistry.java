package org.sosly.witchesandwizards.events;

import net.minecraftforge.common.MinecraftForge;

public class EventRegistry {
    public static void register() {
        MinecraftForge.EVENT_BUS.register(IceBlockEventHandler.class);
    }
}
