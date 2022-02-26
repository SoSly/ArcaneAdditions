/*
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.capabilities.Faction;
import com.mna.api.spells.ComponentApplicationResult;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.sosly.arcaneadditions.capabilities.treestride.ITreestrideCapability;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;
import org.sosly.arcaneadditions.client.menu.TreeStrideMenu;
import org.sosly.arcaneadditions.utils.TreeFinder;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class TreeStrideComponent extends SpellEffect {
    public TreeStrideComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon);
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource spellSource, SpellTarget spellTarget, IModifiedSpellPart<SpellEffect> iModifiedSpellPart, SpellContext spellContext) {
        if (!spellSource.isPlayerCaster() || !spellTarget.isBlock()) {
            return ComponentApplicationResult.FAIL;
        }

        if (!(spellSource.getPlayer() instanceof ServerPlayer player) || !(player.level instanceof ServerLevel level)) {
            return ComponentApplicationResult.FAIL;
        }

        BlockPos pos = spellTarget.getBlock();
        BlockState state = level.getBlockState(pos);

        if (!TreeFinder.isBlockALog(state) || !TreeFinder.isPartOfATree(level, pos, true)) {
            // todo: message = needs to be a tree
            return ComponentApplicationResult.FAIL;
        }

        ITreestrideCapability cap = level.getCapability(TreestrideProvider.TREESTRIDE).orElse(null);
        Map<BlockPos, String> destinations = cap.getPlayerDestinations(player);

        Set<BlockPos> roots = TreeFinder.getRootBlocks(level, pos, blockPos -> TreeFinder.isBlockALog(level, blockPos));
        BlockPos root = findRootBlock(roots, pos);
        ServerPlayer caster = (ServerPlayer)spellSource.getPlayer();

        level.getCapability(TreestrideProvider.TREESTRIDE).ifPresent(treestride -> treestride.setCurrentPosition(caster, root));

        MenuProvider container = new SimpleMenuProvider(TreeStrideMenu::new, new TextComponent("Tree Stride"));
        NetworkHooks.openGui(caster, container);

        return ComponentApplicationResult.SUCCESS;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.EARTH;
    }

    @Override
    public Faction getFactionRequirement() {
        return Faction.FEY_COURT;
    }

    @Override
    public float initialComplexity() {
        return 0;
    }

    @Override
    public int requiredXPForRote() {
        return 0;
    }

    public static BlockPos findRootBlock(Set<BlockPos> blocks, BlockPos start) {
        AtomicReference<BlockPos> root = new AtomicReference<>(start);
        blocks.forEach(block -> {
            if (block.getY() < root.get().getY()) {
                root.set(block);
            }
        });
        return root.get();
    }
}
