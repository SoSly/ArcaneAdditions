package org.sosly.arcaneadditions.rituals.effects;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.api.items.IPhylacteryItem;
import com.mna.api.rituals.IRitualContext;
import com.mna.api.rituals.RitualEffect;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import com.mna.items.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.sosly.arcaneadditions.capabilities.familiar.FamiliarCapability;
import org.sosly.arcaneadditions.capabilities.familiar.FamiliarProvider;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;

import java.util.Iterator;

public class BindFamiliarRitual extends RitualEffect {
    public BindFamiliarRitual(ResourceLocation name) {
        super(name);
    }

    public Component canRitualStart(IRitualContext context) {
        if (context.getCaster() == null) {
            return Component.literal("No player reference found for ritual, aborting.");
        }

        IPlayerProgression p = (IPlayerProgression) context.getCaster().getCapability(PlayerProgressionProvider.PROGRESSION).orElse(null);
        return p != null && p.getTier() >= 3 ? null : Component.literal("You must be at least tier 3 to bind a familiar.");
    }

    protected boolean applyRitualEffect(IRitualContext context) {
        ItemStack phylacteryStack = ItemStack.EMPTY;
        Iterator reagents = context.getCollectedReagents().iterator();
        Player player = context.getCaster();
        Level level = context.getLevel();
        BlockPos pos = context.getCenter();

        while (reagents.hasNext()) {
            ItemStack stack = (ItemStack) reagents.next();
            if (stack.getItem() == ItemInit.CRYSTAL_PHYLACTERY.get()) {
                phylacteryStack = stack;
                break;
            }
        }

        if (phylacteryStack.isEmpty()) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.no_phylactery"));
            return false;
        }

        Item item = phylacteryStack.getItem();
        if (!(item instanceof IPhylacteryItem)) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.no_phylactery"));
            return false;
        }

        IPhylacteryItem phylactery = (IPhylacteryItem) item;
        if (!phylactery.isFull(phylacteryStack)) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.no_phylactery"));
            return false;
        }

        // Remove any existing familiar
        IFamiliarCapability playerCap = player.getCapability(FamiliarProvider.FAMILIAR).orElseGet(FamiliarCapability::new);
        if (playerCap.hasFamiliar()) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.already_bound"));
            if (playerCap.getFamiliar(level) != null) {
                TamableAnimal oldFamiliar = playerCap.getFamiliar(level).get();
                if (oldFamiliar != null) {
                    IFamiliarCapability familiarCap = oldFamiliar.getCapability(FamiliarProvider.FAMILIAR).orElseGet(FamiliarCapability::new);
                    familiarCap.remove();
                    oldFamiliar.remove(Entity.RemovalReason.DISCARDED);
                }
            }
            playerCap.remove();
        }

        // Attempt to summon the familiar
        TamableAnimal familiar = this.bindFamiliar(player, level, phylacteryStack, phylactery, pos);
        if (familiar == null) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.failed"));
            return false;
        }

        // Set the familiar capabilities
        playerCap.setFamiliar(familiar);
        playerCap.setCaster(player);
        IFamiliarCapability familiarCap = familiar.getCapability(FamiliarProvider.FAMILIAR).orElseGet(FamiliarCapability::new);
        familiarCap.setCaster(player);
        familiarCap.setFamiliar(familiar);

        EntityType<?> type = phylactery.getContainedEntity(phylacteryStack);
        MutableComponent name = Component.translatable(type.getDescriptionId());
        player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.success", name));

        return true;
    }

    protected int getApplicationTicks(IRitualContext context) {
        return 20;
    }

    private TamableAnimal bindFamiliar(Player player, Level level, ItemStack stack, IPhylacteryItem phylactery, BlockPos pos) {
        EntityType<?> type = phylactery.getContainedEntity(stack);
        String name = player.getDisplayName().getString();
        MutableComponent familiarName = Component.literal(name)
                .append("'s ")
                .append(Component.translatable(type.getDescriptionId()))
                .append(" Familiar");
        TamableAnimal familiar = (TamableAnimal) type.create(level);
        familiar.setPos(Vec3.atBottomCenterOf(pos.above()));

        if (!level.addFreshEntity(familiar)) {
            return null;
        }

        familiar.setOwnerUUID(player.getUUID());
        familiar.setTame(true);
        familiar.setCustomName(familiarName);
        familiar.setCustomNameVisible(true);
        familiar.goalSelector.removeAllGoals(a -> true);
        familiar.goalSelector.addGoal(1, new FloatGoal(familiar));
        familiar.goalSelector.addGoal(1, new PanicGoal(familiar, 1.5D));
        familiar.goalSelector.addGoal(2, new SitWhenOrderedToGoal(familiar));
        familiar.goalSelector.addGoal(6, new FollowOwnerGoal(familiar, 1.0D, 10.0F, 5.0F, false));
        familiar.goalSelector.addGoal(8, new LeapAtTargetGoal(familiar, 0.3F));
        familiar.goalSelector.addGoal(9, new OcelotAttackGoal(familiar));
        familiar.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(familiar, 0.8D, 1.0000001E-5F));
        familiar.goalSelector.addGoal(12, new LookAtPlayerGoal(familiar, Player.class, 10.0F));

        return familiar;
    }
}
