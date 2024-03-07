package org.sosly.arcaneadditions.events.entities;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.familiar.FamiliarCapability;
import org.sosly.arcaneadditions.capabilities.familiar.FamiliarProvider;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FamiliarEvents {
    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<?> event) {
        event.addCapability(IFamiliarCapability.FAMILIAR_CAPABILITY, new FamiliarProvider());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof TamableAnimal animal) {
            if (animal.getCapability(FamiliarProvider.FAMILIAR).isPresent()) {
                IFamiliarCapability famCap = animal.getCapability(FamiliarProvider.FAMILIAR).orElseGet(FamiliarCapability::new);
                Player caster = famCap.getCaster(animal.level()).get();
                IFamiliarCapability playerCap = caster.getCapability(FamiliarProvider.FAMILIAR).orElseGet(FamiliarCapability::new);
                famCap.remove();
                playerCap.remove();
            }
        }
    }
}
