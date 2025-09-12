package org.sosly.arcaneadditions.events.entities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.familiar.FamiliarProvider;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FamiliarLifecycleHandler {

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<?> event) {
        if (!(event.getObject() instanceof Player)) {
            return;
        }

        event.addCapability(IFamiliarCapability.FAMILIAR_CAPABILITY, new FamiliarProvider());
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Mob mob && FamiliarHelper.isFamiliar(mob) && FamiliarHelper.isOrphaned(mob)) {
            mob.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
        if (cap == null) {
            return;
        }

        if (cap.getType() == null || cap.isBapped()) {
            return;
        }

        Mob existingFamiliar = cap.getFamiliar();

        if (existingFamiliar != null && !existingFamiliar.isRemoved() &&
            existingFamiliar.level().dimension().equals(player.level().dimension())) {
            return;
        }

        if (existingFamiliar != null && existingFamiliar.isRemoved()) {
            cap.setFamiliar(null);
            cap.setFamiliarUUID(null);
        }

        if (existingFamiliar != null &&
            !existingFamiliar.level().dimension().equals(player.level().dimension()) &&
            cap.isOrderedToStay()) {
            return;
        }

        cap.setCaster(player);
        cap.loadOnNextTick(event.getLevel(), player.blockPosition());
    }

    @SubscribeEvent
    public static void onLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
        if (cap == null) {
            return;
        }

        if (cap.isOrderedToStay()) {
            return;
        }

        Mob familiar = cap.getFamiliar();
        if (familiar == null) {
            return;
        }

        familiar.remove(Entity.RemovalReason.DISCARDED);
        cap.setFamiliar(null);
        cap.setFamiliarUUID(null);
    }

    @SubscribeEvent
    public static void onFamiliarTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
            if (cap == null) {
                continue;
            }

            cap.tick();
        }
    }

    @SubscribeEvent
    public static void onFamiliarDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !FamiliarHelper.isFamiliar(mob)) {
            return;
        }

        Player caster = FamiliarHelper.getCaster(mob);
        if (caster == null) {
            return;
        }

        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(caster);
        if (cap == null) {
            return;
        }

        cap.reset();
    }
}
