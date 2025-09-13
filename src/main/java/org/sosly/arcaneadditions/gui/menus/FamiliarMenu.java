/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.gui.menus;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.gui.MenuRegistry;
import org.sosly.arcaneadditions.spells.FamiliarSpell;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamiliarMenu extends AbstractContainerMenu {
    private final IFamiliarCapability capability;
    private final Mob familiar;
    
    // Client-side data
    private UUID familiarUuid;
    private String familiarName;
    private String familiarType;
    private String familiarRegistryName;
    private float familiarHealth;
    private float familiarMaxHealth;
    private float mana;
    private float maxMana;
    private CompoundTag familiarNbt;
    private List<SpellData> spells = new ArrayList<>();
    
    public static class SpellData {
        public final Component name;
        public final float manaCost;
        public final FamiliarSpell.Frequency frequency;
        public final boolean offensive;
        
        public SpellData(Component name, float manaCost, FamiliarSpell.Frequency frequency, boolean offensive) {
            this.name = name;
            this.manaCost = manaCost;
            this.frequency = frequency;
            this.offensive = offensive;
        }
    }

    public FamiliarMenu(int id, Inventory playerInv) {
        this(id, playerInv, FamiliarHelper.getFamiliarCapability(playerInv.player));
    }

    public FamiliarMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id, playerInv, (IFamiliarCapability) null);
        
        // Read network data
        this.familiarUuid = extraData.readUUID();
        this.familiarName = extraData.readUtf();
        this.familiarType = extraData.readUtf();
        this.familiarRegistryName = extraData.readUtf();
        this.familiarHealth = extraData.readFloat();
        this.familiarMaxHealth = extraData.readFloat();
        this.mana = extraData.readFloat();
        this.maxMana = extraData.readFloat();
        this.familiarNbt = extraData.readNbt();
        
        int spellCount = extraData.readInt();
        for (int i = 0; i < spellCount; i++) {
            Component name = extraData.readComponent();
            float manaCost = extraData.readFloat();
            FamiliarSpell.Frequency frequency = extraData.readEnum(FamiliarSpell.Frequency.class);
            boolean offensive = extraData.readBoolean();
            
            this.spells.add(new SpellData(name, manaCost, frequency, offensive));
        }
    }

    public FamiliarMenu(int id, Inventory playerInv, IFamiliarCapability capability) {
        super(MenuRegistry.FAMILIAR.get(), id);
        this.capability = capability;
        this.familiar = capability != null ? capability.getFamiliar() : null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        // On client side, we only have network data, so always return true
        if (player.level().isClientSide()) {
            return true;
        }
        
        // Server-side validation
        if (capability == null || familiar == null) {
            return false;
        }
        
        if (!capability.getCaster().equals(player)) {
            return false;
        }
        
        return familiar.isAlive() && player.distanceToSqr(familiar) < 64.0;
    }

    public IFamiliarCapability getCapability() {
        return capability;
    }

    public Mob getFamiliar() {
        return familiar;
    }
    
    // Client-side data getters
    public boolean hasClientData() {
        return familiarUuid != null;
    }
    
    public String getFamiliarName() {
        return familiarName;
    }
    
    public String getFamiliarType() {
        return familiarType;
    }
    
    public String getFamiliarRegistryName() {
        return familiarRegistryName;
    }
    
    public float getFamiliarHealth() {
        return familiarHealth;
    }
    
    public float getFamiliarMaxHealth() {
        return familiarMaxHealth;
    }
    
    public float getMana() {
        return mana;
    }
    
    public float getMaxMana() {
        return maxMana;
    }
    
    public List<SpellData> getSpellData() {
        return spells;
    }
    
    public CompoundTag getFamiliarNbt() {
        return familiarNbt;
    }
    
    public void updateFromServerData() {
        if (capability != null && familiar != null) {
            this.familiarHealth = familiar.getHealth();
            this.familiarMaxHealth = familiar.getMaxHealth();
            this.mana = capability.getCastingResource().getAmount();
            this.maxMana = capability.getCastingResource().getMaxAmount();
        }
    }
    
    public void updateClientData(float health, float maxHealth, float mana, float maxMana) {
        this.familiarHealth = health;
        this.familiarMaxHealth = maxHealth;
        this.mana = mana;
        this.maxMana = maxMana;
    }
}