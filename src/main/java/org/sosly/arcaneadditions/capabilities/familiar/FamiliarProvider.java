package org.sosly.arcaneadditions.capabilities.familiar;

import com.mna.spells.crafting.SpellRecipe;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sosly.arcaneadditions.spells.FamiliarSpell;

public class FamiliarProvider implements ICapabilitySerializable<Tag> {
    public static final Capability<IFamiliarCapability> FAMILIAR = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<IFamiliarCapability> holder = LazyOptional.of(FamiliarCapability::new);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return FAMILIAR.orEmpty(cap, this.holder);
    }

    @Override
    public Tag serializeNBT() {
        IFamiliarCapability instance = this.holder.orElse(new FamiliarCapability());
        CompoundTag nbt = new CompoundTag();
        if (instance.getCaster() != null) {
            nbt.putUUID("caster", instance.getCaster().getUUID());
        }
        if (instance.isBapped()) {
            nbt.putBoolean("bapped", instance.isBapped());
        }
        if (instance.getCastingResource() != null) {
            nbt.putString("castingResourceId", instance.getCastingResource().getRegistryName().toString());
            instance.getCastingResource().writeNBT(nbt);
        }
        if (instance.getFamiliar() != null) {
            nbt.putUUID("familiar", instance.getFamiliar().getUUID());
        }
        if (instance.getLastInteract() > 0) {
            nbt.putLong("lastInteract", instance.getLastInteract());
        }
        if (!instance.getName().isEmpty()) {
            nbt.putString("name", instance.getName());
        }
        if (instance.getType() != null) {
            nbt.putString("type", ForgeRegistries.ENTITY_TYPES.getResourceKey(instance.getType()).get().location().toString());
        }
        if (instance.isOrderedToStay()) {
            nbt.putBoolean("orderedToStay", instance.isOrderedToStay());
        }
        if (instance.getFamiliarNBT() != null) {
            nbt.put("familiarNBT", instance.getFamiliarNBT());
        }
        if (!instance.getSpellsKnown().isEmpty()) {
            CompoundTag spells = new CompoundTag();
            int i = 0;
            for (FamiliarSpell spell : instance.getSpellsKnown()) {
                CompoundTag snbt = new CompoundTag();
                snbt.putBoolean("offensive", spell.isOffensive());
                snbt.putInt("frequency", spell.getFrequency().ordinal());
                snbt.putString("name", spell.getName().getString());
                CompoundTag rnbt = new CompoundTag();
                spell.getRecipe().writeToNBT(rnbt);
                snbt.put("recipe", rnbt);
                spells.put("spell_" + i++, snbt);
            }
            nbt.put("spells", spells);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        IFamiliarCapability instance = this.holder.orElse(new FamiliarCapability());
        if (nbt instanceof CompoundTag cnbt) {
            if (cnbt.contains("bapped")) {
                instance.setBapped(cnbt.getBoolean("bapped"));
            }
            if (cnbt.contains("castingResourceId")) {
                instance.setCastingResourceType(new ResourceLocation(cnbt.getString("castingResourceId")));
            }
            instance.getCastingResource().readNBT(cnbt);
            instance.getCastingResource().setNeedsSync();
            if (instance.getFamiliar() != null) {
                instance.setFamiliarUUID(cnbt.getUUID("familiar"));
            }
            if (cnbt.contains("lastInteract")) {
                instance.setLastInteract(cnbt.getLong("lastInteract"));
            }
            if (cnbt.contains("orderedToStay")) {
                instance.setOrderedToStay(cnbt.getBoolean("orderedToStay"));
            }
            if (cnbt.contains("name")) {
                instance.setName(cnbt.getString("name"));
            }
            if (cnbt.contains("type")) {
                EntityType<?> type = EntityType.byString(cnbt.getString("type")).orElse(null);
                if (type == null) {
                    throw new RuntimeException("Could not get type for Familiar.");
                }
                instance.setType((EntityType<? extends Mob>) type);
            }
            if (cnbt.contains("familiarNBT")) {
                instance.setFamiliarNBT(cnbt.getCompound("familiarNBT"));
            }
            if (cnbt.contains("spells")) {
                CompoundTag spells = cnbt.getCompound("spells");
                for (String key : spells.getAllKeys()) {
                    CompoundTag snbt = spells.getCompound(key);
                    SpellRecipe recipe = SpellRecipe.fromNBT(snbt.getCompound("recipe"));
                    if (recipe.getShape() == null) {
                        continue;
                    }
                    boolean offensive = snbt.getBoolean("offensive");
                    FamiliarSpell.Frequency frequency = FamiliarSpell.Frequency.values()[snbt.getInt("frequency")];
                    Component name = Component.literal(snbt.getString("name"));
                    FamiliarSpell spell = new FamiliarSpell(name, recipe, frequency, offensive);
                    instance.addSpellKnown(spell, false);
                }
            }
        }
    }
}
