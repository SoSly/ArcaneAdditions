package org.sosly.arcaneadditions.events.entities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
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
        
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(event.getEntity());
        if (cap == null) {
            return;
        }

        Mob familiar = cap.getFamiliar();
        if (familiar == null || !familiar.equals(event.getTarget())) {
            return;
        }

        if (!cap.getCaster().equals(event.getEntity())) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();
        
        if (event.getEntity().isShiftKeyDown()) {
            if (event.getSide() != LogicalSide.SERVER) {
                event.setCanceled(true);
                return;
            }
            
            MenuProvider menuProvider = new MenuProvider() {
                @Override
                public net.minecraft.network.chat.Component getDisplayName() {
                    return net.minecraft.network.chat.Component.literal("Familiar");
                }

                @Override
                public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory playerInv, net.minecraft.world.entity.player.Player player) {
                    return new org.sosly.arcaneadditions.gui.menus.FamiliarMenu(id, playerInv, cap);
                }
            };

            net.minecraftforge.network.NetworkHooks.openScreen(player, menuProvider, buf -> {
                buf.writeUUID(familiar.getUUID());
                buf.writeUtf(cap.getName());
                buf.writeUtf(familiar.getType().getDescriptionId());
                buf.writeUtf(net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(familiar.getType()).toString());
                buf.writeFloat(familiar.getHealth());
                buf.writeFloat(familiar.getMaxHealth());
                buf.writeFloat(cap.getCastingResource().getAmount());
                buf.writeFloat(cap.getCastingResource().getMaxAmount());
                buf.writeNbt(familiar.saveWithoutId(new net.minecraft.nbt.CompoundTag()));
                buf.writeInt(cap.getSpellsKnown().size());
                for (org.sosly.arcaneadditions.spells.FamiliarSpell spell : cap.getSpellsKnown()) {
                    buf.writeComponent(spell.getName());
                    buf.writeFloat(spell.getRecipe().getManaCost());
                    buf.writeEnum(spell.getFrequency());
                    buf.writeBoolean(spell.isOffensive());
                }
            });
            event.setCanceled(true);
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