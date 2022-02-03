package org.sosly.arcaneadditions.capabilities;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.*;
import org.sosly.arcaneadditions.*;
import org.sosly.arcaneadditions.capabilities.polymorph.IPolymorphCapability;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityRegistry {
    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IPolymorphCapability.class);
    }
}
