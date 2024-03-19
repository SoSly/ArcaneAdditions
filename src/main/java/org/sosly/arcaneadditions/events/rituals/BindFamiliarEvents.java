package org.sosly.arcaneadditions.events.rituals;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BindFamiliarEvents {
    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob mob)) {
            return;
        }

        if (FamiliarHelper.isFamiliar(mob)) {
            Player owner = FamiliarHelper.getCaster(mob);
             FamiliarHelper.addFamiliarAI(mob, owner);
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() == LogicalSide.CLIENT) {
            return;
        }

        Entity target = event.getTarget();
        if (!FamiliarHelper.isFamiliar(target)) {
            return;
        }

        Mob familiar = (Mob) target;
        Player player = event.getEntity();
        if (!FamiliarHelper.isCaster(familiar, player)) {
           return;
        }

        FamiliarHelper.setInStayingPose(familiar, !FamiliarHelper.isOrderedToStay(familiar));
    }
}
