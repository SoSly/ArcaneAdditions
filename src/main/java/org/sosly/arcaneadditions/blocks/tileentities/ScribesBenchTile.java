package org.sosly.arcaneadditions.blocks.tileentities;

import com.mna.advancements.CustomAdvancementTriggers;
import com.mna.api.events.GenericProgressionEvent;
import com.mna.api.events.ProgressionEventIDs;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ICanContainSpell;
import com.mna.api.spells.base.ISpellDefinition;
import com.mna.api.tools.MATags;
import com.mna.blocks.tileentities.wizard_lab.WizardLabTile;
import com.mna.items.ItemInit;
import com.mna.items.artifice.charms.ItemContingencyCharm;
import com.mna.items.sorcery.ItemSpell;
import com.mna.spells.crafting.SpellRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import org.sosly.arcaneadditions.blocks.TileEntityRegistry;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ScribesBenchTile extends WizardLabTile {
    public static final int SLOT_INK = 0;
    public static final int SLOT_LAPIS = 1;
    public static final int SLOT_SPELL = 2;
    public static final int SLOT_VELLUM = 3;
    public static final int INVENTORY_SIZE = 4;
    private static final int LAPIS_REQUIRED_PER_TIER = 10;
    private static final int LAPIS_REQUIRED_PER_TIER_RECHARGE = 2;
    private static final int LAPIS_REQUIRED_PER_TIER_GLYPH = 1;
    private static final int INK_REQUIRED_PER_TIER = 1;
    private float ticksRequired = 100.0F;
    private SpellRecipe copyRecipe;


    public ScribesBenchTile(BlockPos pos, BlockState state) {
        super(TileEntityRegistry.SCRIBES_BENCH.get(), pos, state, INVENTORY_SIZE);
    }

    @Override
    public boolean canActivate(Player player) {
        ItemStack vellumItem = this.getItem(SLOT_VELLUM);
        ItemStack spellItem = this.getItem(SLOT_SPELL);

        if (spellItem.isEmpty() || !(spellItem.getItem() instanceof ICanContainSpell)) {
            return false;
        }

        ISpellDefinition spell = ((ICanContainSpell)spellItem.getItem()).getSpell(spellItem, player);
        return this.hasStack(3) && this.hasStack(SLOT_INK)
                && this.getItem(SLOT_INK).getDamageValue() + this.getInkRequired() <= this.getItem(SLOT_INK).getMaxDamage()
                && this.hasStack(SLOT_LAPIS) && this.getItem(SLOT_LAPIS).getCount() >= this.getLapisRequired(player)
                && this.hasStack(SLOT_VELLUM) && vellumItem.getItem() instanceof ICanContainSpell
                && ((ICanContainSpell)vellumItem.getItem()).canAcceptSpell(vellumItem, spell);
    }

    @Override
    protected boolean canContinue() {
        ItemStack vellumItem = this.getItem(SLOT_VELLUM);
        ItemStack spellItem = this.getItem(SLOT_SPELL);

        if (spellItem.isEmpty()) {
            return false;
        }

        ISpellDefinition spell = ((ICanContainSpell)spellItem.getItem()).getSpell(spellItem, this.getCrafter());
        return this.copyRecipe != null && this.hasStack(SLOT_VELLUM) && this.hasStack(SLOT_SPELL)
                && vellumItem.getItem() instanceof ICanContainSpell
                && ((ICanContainSpell)vellumItem.getItem()).canAcceptSpell(vellumItem, spell);
    }

    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (this.isActive()) {
            return false;
        } else if (index != SLOT_VELLUM) {
            return true;
        } else {
            return direction == Direction.UP || direction == Direction.DOWN;
        }
    }

    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (this.isActive()) {
            return false;
        } else {
            ItemStack existing = this.getItem(index);
            switch (index) {
                case 0:
                    return stack.getItem() == ItemInit.ARCANIST_INK.get() && (existing.isEmpty() || existing.getCount() < existing.getMaxStackSize());
                case 1:
                    return stack.getItem() == Items.LAPIS_LAZULI && (existing.isEmpty() || existing.getCount() < existing.getMaxStackSize());
                case 2:
                default:
                    return false;
                case 3:
                    return stack.getItem() instanceof ICanContainSpell && existing.isEmpty();
            }
        }
    }

    @Override
    public float getPctComplete() {
        return (float)this.getActiveTicks() / this.ticksRequired;
    }

    protected boolean canActiveTick() {
        if (this.copyRecipe == null || !this.copyRecipe.isValid()) {
            if (!this.getLevel().isClientSide()) {
                this.setInactive();
            }
            return false;
        }

        return true;
    }

    public int getInkRequired() {
        return this.copyRecipe != null && this.hasStack(3) ? this.copyRecipe.getTier(this.level) : 0;
    }

    public int getLapisRequired(@Nullable Player player) {
        if (this.copyRecipe != null && this.hasStack(SLOT_VELLUM)) {
            ItemStack inputStack = this.getItem(SLOT_VELLUM);
            if (inputStack.getItem() instanceof ItemContingencyCharm) {
                ISpellDefinition spell = ((ICanContainSpell)inputStack.getItem()).getSpell(inputStack, player);
                if (this.copyRecipe.isSame(spell, false, true, true)) {
                    return this.copyRecipe.getTier(this.level) * LAPIS_REQUIRED_PER_TIER_RECHARGE;
                }
            }

            return MATags.isItemIn(inputStack.getItem(), MATags.Items.STONE_RUNES) ? this.copyRecipe.getTier(this.level) * LAPIS_REQUIRED_PER_TIER_GLYPH : this.copyRecipe.getTier(this.level) * LAPIS_REQUIRED_PER_TIER;
        } else {
            return 0;
        }
    }

    protected CompoundTag getMeta() {
        CompoundTag tag = new CompoundTag();
        if (this.copyRecipe != null) {
            this.copyRecipe.writeToNBT(tag);
        }
        return tag;
    }

    public int[] getSlotsForFace(Direction side) {
        return new int[]{SLOT_INK, SLOT_LAPIS, SLOT_VELLUM};
    }

    public int getXPCost(Player crafter) {
        return 20;
    }

    protected void loadMeta(CompoundTag tag) {
        this.copyRecipe = SpellRecipe.fromNBT(tag);
    }

    protected void onComplete() {
        ItemStack output = this.getItem(3).copy();
        if (!output.isEmpty()) {
            output = ((ICanContainSpell)output.getItem()).setSpell(output, this.copyRecipe);
            ((ICanContainSpell)output.getItem()).setTranscribedSpell(output);
            if (this.getItem(SLOT_SPELL).hasCustomHoverName()) {
                output.setHoverName(this.getItem(SLOT_SPELL).getHoverName());
            }

            this.setItem(SLOT_VELLUM, output);
            if (this.getCrafter() != null && !this.getLevel().isClientSide()) {
                Player crafter = this.getCrafter();
                if (crafter != null) {
                    MinecraftForge.EVENT_BUS.post(new GenericProgressionEvent(crafter, ProgressionEventIDs.TRANSCRIBE_SPELL));
                    if (crafter instanceof ServerPlayer) {
                        CustomAdvancementTriggers.TRANSCRIBE_SPELL.trigger((ServerPlayer)crafter, this.copyRecipe, output);
                    }
                }
            }

            if (!this.getLevel().isClientSide()) {
                this.getLevel().playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), SFX.Event.Eldrin.DRAW_IN_ITEM, SoundSource.BLOCKS, 1.0F, (float)(0.95 + Math.random() * 0.10000000149011612));
            }

        }
    }

    protected void onCraftStart(Player crafter) {
        ItemStack book = this.getItem(SLOT_SPELL);
        if (!book.isEmpty() && book.getItem() instanceof ItemSpell spell) {
            ItemStack input = this.getItem(SLOT_VELLUM);
            if (!input.isEmpty() && input.getItem() instanceof ICanContainSpell) {
                ItemStack lapis = this.getItem(SLOT_LAPIS);
                lapis.shrink(this.getLapisRequired(crafter));
                ItemStack ink = this.getItem(SLOT_INK);
                if (!this.getLevel().isClientSide()) {
                    ink.hurt(this.getInkRequired(), this.level.random, (ServerPlayer)crafter);
                    if (ink.getDamageValue() >= ink.getMaxDamage()) {
                        this.setItem(0, ItemStack.EMPTY);
                    }
                }

                if (!this.getLevel().isClientSide()) {
                    CompoundTag spellTag = spell.getSpellCompound(book, crafter);
                    this.copyRecipe = SpellRecipe.fromNBT(spellTag);
                    if (!this.copyRecipe.isValid()) {
                        this.setInactive();
                    } else {
                        // we're good, so what now?
                    }
                }

            } else {
                this.setInactive();
            }
        } else {
            this.setInactive();
        }
    }

    @Override
    protected List<Integer> getSyncedInventorySlots() {
        return Arrays.asList(SLOT_INK, SLOT_LAPIS, SLOT_SPELL, SLOT_VELLUM);
    }


}
