package org.sosly.arcaneadditions.cantrips;

import com.mna.api.cantrips.ICantrip;
import com.mna.cantrips.CantripRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;
import org.sosly.arcaneadditions.utils.RLoc;

public class Cantrips {
    private static final ResourceLocation BOLT = new ResourceLocation("mna", "manaweave_patterns/bolt");
    private static final ResourceLocation CIRCLE = new ResourceLocation("mna", "manaweave_patterns/circle");
    private static final ResourceLocation INVERSE_TRIANGLE = new ResourceLocation("mna", "manaweave_patterns/inverted_triangle");

    public static void registerCantrips() {
        CantripRegistry.INSTANCE.registerCantrip(RLoc.create("cantrips/unbap"),
                RLoc.create("textures/gui/cantrips/unbap.png"), 3, Cantrips::summonFamiliarAfterBap,
                ItemStack.EMPTY, BOLT, INVERSE_TRIANGLE, CIRCLE).setRequiredAdvancement(RLoc.create("summon_familiar"));
    }

    public static void summonFamiliarAfterBap(Player player, ICantrip cantrip, InteractionHand hand) {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
        if (cap == null || !cap.isBapped()) {
            return;
        }
        FamiliarHelper.createFamiliar(player, cap.getType(), Component.literal(cap.getName()), player.level(), player.getOnPos());
    }
}
