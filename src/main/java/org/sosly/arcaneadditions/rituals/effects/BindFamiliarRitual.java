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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.configs.Config;
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

        IPlayerProgression p = context.getCaster().getCapability(PlayerProgressionProvider.PROGRESSION).orElse(null);
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
            ItemStack itemStack = reagents.next();
            if (itemStack.getItem() == ItemInit.CRYSTAL_PHYLACTERY.get()) {
                stack = itemStack;
                break;
            }
        }

        if (stack.isEmpty()) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind_familiar.no_phylactery"));
            return false;
        }

        Item item = stack.getItem();
        if (!(item instanceof IPhylacteryItem phylactery)) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind_familiar.no_phylactery"));
            return false;
        }

        if (!phylactery.isFull(stack)) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind_familiar.no_phylactery"));
            return false;
        }

        EntityType<? extends Mob> type = phylactery.getContainedEntity(stack);
        if (type == null) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind_familiar.no_phylactery"));
            return false;
        }

        if (!Config.SERVER.familiar.familiars.get().contains(EntityType.getKey(type).toString())) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind_familiar.invalid_familiar"));
            return false;
        }

        // Check if the player already has a familiar
        if (FamiliarHelper.hasFamiliar(player)) {
            FamiliarHelper.removeFamiliar(player);
        }
        String name = player.getDisplayName().getString();
        MutableComponent familiarName = Component.literal(name)
                .append("'s ")
                .append(Component.translatable(type.getDescriptionId()))
                .append(" Familiar");
        if (!FamiliarHelper.createFamiliar(player, type, familiarName, level, pos)) {
            player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind_familiar.failed"));
            return false;
        }
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
        if (cap != null) {
            cap.getCastingResource().setAmount(0);
        }
        player.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind_familiar.success", name));
        return true;
    }

    protected int getApplicationTicks(IRitualContext context) {
        return 20;
    }
}
