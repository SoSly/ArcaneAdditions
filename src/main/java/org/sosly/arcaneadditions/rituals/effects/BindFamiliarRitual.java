package org.sosly.arcaneadditions.rituals.effects;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.api.items.IPhylacteryItem;
import com.mna.api.rituals.IRitualContext;
import com.mna.api.rituals.RitualEffect;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import com.mna.entities.ai.TargetDefendOwnerGoal;
import com.mna.items.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.sosly.arcaneadditions.configs.Config;
import org.sosly.arcaneadditions.entities.ai.FollowOwnerGoal;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

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
        ItemStack stack = ItemStack.EMPTY;
        Iterator<ItemStack> reagents = context.getCollectedReagents().iterator();
        Player player = context.getCaster();
        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        ResourceKey<Level> dimension = player.level().dimension();
        ServerLevel level = server.getLevel(dimension);
        if (level == null) {
            return false;
        }

        BlockPos pos = context.getCenter();

        while (reagents.hasNext()) {
            ItemStack itemStack = (ItemStack) reagents.next();
            if (itemStack.getItem() == ItemInit.CRYSTAL_PHYLACTERY.get()) {
                stack = itemStack;
                break;
            }
        }

        if (stack.isEmpty()) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.no_phylactery"));
            return false;
        }

        Item item = stack.getItem();
        if (!(item instanceof IPhylacteryItem)) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.no_phylactery"));
            return false;
        }

        IPhylacteryItem phylactery = (IPhylacteryItem) item;
        if (!phylactery.isFull(stack)) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.no_phylactery"));
            return false;
        }

        EntityType<?> type = phylactery.getContainedEntity(stack);
        if (type == null) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.no_phylactery"));
            return false;
        }

        if (!Config.SERVER.familiar.familiars.get().contains(EntityType.getKey(type).toString())) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.invalid_familiar"));
            return false;
        }

        // Check if the player already has a familiar
        Mob oldFamiliar = FamiliarHelper.getFamiliar(player);
        if (oldFamiliar != null) {
            oldFamiliar.remove(Entity.RemovalReason.DISCARDED);
            player.getPersistentData().remove(FamiliarHelper.FAMILIAR);
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.already_bound"));
        }

        // Bind the familiar, then perform a last minute sanity check before attempting to add it to the level
        Mob familiar = bindFamiliar(player, level, type, pos);
        if (!(familiar instanceof PathfinderMob) || !level.addFreshEntity(familiar)) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.failed"));
            return false;
        }

        familiar.getPersistentData().putUUID(FamiliarHelper.CASTER, player.getUUID());
        player.getPersistentData().putUUID(FamiliarHelper.FAMILIAR, familiar.getUUID());
        Component name = type.getDescription();
        player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind-familiar.success", name));

        return true;
    }

    protected int getApplicationTicks(IRitualContext context) {
        return 20;
    }

    private PathfinderMob bindFamiliar(Player player, ServerLevel level, EntityType<?> type, BlockPos pos) {
        String name = player.getDisplayName().getString();
        MutableComponent familiarName = Component.literal(name)
                .append("'s ")
                .append(Component.translatable(type.getDescriptionId()))
                .append(" Familiar");
        PathfinderMob familiar = (PathfinderMob) type.create(level);
        if (familiar == null) {
            return null;
        }
        familiar.setPos(Vec3.atBottomCenterOf(pos.above()));

        familiar.setCustomName(familiarName);
        familiar.setCustomNameVisible(true);
        FamiliarHelper.setFamiliar(familiar);

        return familiar;
    }
}
