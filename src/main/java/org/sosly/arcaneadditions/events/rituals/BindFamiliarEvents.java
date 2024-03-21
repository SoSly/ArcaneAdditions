package org.sosly.arcaneadditions.events.rituals;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.familiar.FamiliarProvider;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BindFamiliarEvents {
    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<?> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(IFamiliarCapability.FAMILIAR_CAPABILITY, new FamiliarProvider());
        }
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
        if (cap == null) {
            return;
        }

        if (cap.getType() != null) {
            FamiliarHelper.createFamiliar(player, cap.getType(), Component.literal(cap.getName()), event.getLevel(), player.getOnPos());
        }
    }

    @SubscribeEvent
    public static void onLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        Mob familiar = FamiliarHelper.getFamiliar(player);
        if (familiar != null) {
            familiar.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
            IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);

            if (cap != null) {
                cap.setFamiliar(null);
                cap.setFamiliarUUID(null);
            }
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() == LogicalSide.CLIENT) {
            return;
        }

        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(event.getEntity());
        if (cap == null) {
            return;
        }

        if (!cap.getFamiliar().equals(event.getTarget())) {
            return;
        }

        // spam prevention
        if (cap.getLastInteract() > event.getLevel().getGameTime() - 20L) {
            return;
        }

        cap.setOrderedToStay(!cap.isOrderedToStay());
    }

    @SubscribeEvent
    public static void onFamiliarTick(TickEvent.ServerTickEvent event) {
        for(ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            player.getCapability(FamiliarProvider.FAMILIAR).ifPresent(IFamiliarCapability::tick);
            IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
            if (cap != null) {
                cap.tick();
            }
        }
    }
}
