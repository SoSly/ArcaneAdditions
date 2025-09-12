package org.sosly.arcaneadditions.events.entities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FamiliarInteractionHandler {

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() == LogicalSide.CLIENT) {
            return;
        }

        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(event.getEntity());
        if (cap == null) {
            return;
        }

        Mob familiar = cap.getFamiliar();
        if (familiar == null || !familiar.equals(event.getTarget())) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!cap.getCaster().equals(player)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.getTags().anyMatch(tag -> tag.location().equals(RLoc.create("can_bap_familiars")))) {
            cap.setBapped(true);
            familiar.remove(Entity.RemovalReason.DISCARDED);
            event.setCanceled(true);
            return;
        }

        if (cap.getLastInteract() > event.getLevel().getGameTime() - 20L) {
            return;
        }

        cap.setOrderedToStay(!cap.isOrderedToStay());
    }

    @SubscribeEvent
    public static void onFamiliarTravelToDimension(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !FamiliarHelper.isFamiliar(mob)) {
            return;
        }

        event.setCanceled(true);
    }
}