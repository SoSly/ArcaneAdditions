package org.sosly.arcaneadditions.events.rituals;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import org.sosly.arcaneadditions.utils.RLoc;

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

        // resummon the player's familiar if they have one
        if (entity instanceof ServerPlayer player) {
            IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
            if (cap == null) {
                return;
            }

            if (cap.getType() != null && !cap.isBapped()) {
                FamiliarHelper.createFamiliar(player, cap.getType(), Component.literal(cap.getName()), event.getLevel(), player.getOnPos());
            }
        }

        // ensure this isn't a familiar missing a player
        if (entity instanceof Mob mob && FamiliarHelper.isFamiliar(mob)) {
            IFamiliarCapability fCap = FamiliarHelper.getFamiliarCapability(mob);
            if (fCap == null) {
                mob.remove(Entity.RemovalReason.DISCARDED);
                return;
            }

            if (fCap.getCaster() == null) {
                mob.remove(Entity.RemovalReason.DISCARDED);
                return;
            }

            IFamiliarCapability pCap = FamiliarHelper.getFamiliarCapability(fCap.getCaster());
            if (pCap == null) {
                mob.remove(Entity.RemovalReason.DISCARDED);
                return;
            }

            if (pCap.getFamiliar() != mob) {
                mob.remove(Entity.RemovalReason.DISCARDED);
                return;
            }
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

        Mob familiar = cap.getFamiliar();
        if (!familiar.equals(event.getTarget())) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!cap.getCaster().equals(player)) {
            return;
        }

        // bap!
        // bolt, upside down triangle, circle (I _call you down_, _here_, _from anywhere_) cantrip to reverse
        ItemStack stack = event.getItemStack();
        if (stack.getTags().anyMatch(tag -> tag.location().equals(RLoc.create("can_bap_familiars")))) {
            cap.setBapped(true);
            familiar.remove(Entity.RemovalReason.DISCARDED);
            event.setCanceled(true);
            return;
        }

        // spam prevention
        if (cap.getLastInteract() > event.getLevel().getGameTime() - 20L) {
            return;
        }

        // osuwari!
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
