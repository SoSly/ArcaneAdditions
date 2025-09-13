/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.gui.screens;

import com.mna.items.ItemInit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.sosly.arcaneadditions.gui.menus.FamiliarMenu;
import org.sosly.arcaneadditions.gui.widgets.SpellRemoveButton;
import org.sosly.arcaneadditions.networking.PacketHandler;
import org.sosly.arcaneadditions.networking.messages.serverbound.RemoveFamiliarSpell;
import org.sosly.arcaneadditions.networking.messages.serverbound.RequestFamiliarDataUpdate;
import org.sosly.arcaneadditions.spells.FamiliarSpell;
import org.sosly.arcaneadditions.utils.RLoc;

import java.awt.*;
import java.util.List;

public class FamiliarScreen extends AbstractContainerScreen<FamiliarMenu> {
    private static final ResourceLocation TEXTURE = RLoc.create("textures/gui/familiar.png");
    
    private static final int INFO_LABEL_X = 60;
    private static final int INFO_VALUE_X = 100;
    private static final int OWNER_Y = 17;
    private static final int TYPE_Y = 27;
    private static final int HEALTH_Y = 37;
    private static final int MANA_Y = 47;
    private static final int SPELLS_LABEL_Y = 57;
    private static final int SPELLS_START_Y = 67;
    private static final int SPELL_LINE_HEIGHT = 10;
    private static final int SPELL_ICON_X = 60;
    private static final int SPELL_TEXT_X = 70;
    private static final int SPELL_INGOT_X = 165;
    private static final int SPELL_DELETE_X = 185;
    
    private static final int ENTITY_DISPLAY_X = 30;
    private static final int ENTITY_DISPLAY_Y = 60;
    private static final int ENTITY_SCALE = 30;
    private static final int ENTITY_MOUSE_OFFSET_X = 20;
    private static final int UPDATE_INTERVAL_TICKS = 20;
    
    private int tickCounter = 0;
    private final java.util.Map<String, SpellRemoveButton> spellButtons = new java.util.HashMap<>();

    public FamiliarScreen(FamiliarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 199;
        this.imageHeight = 119;
        this.titleLabelX = 8;
        this.titleLabelY = 5;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
    
    @Override
    public void init() {
        super.init();
        
        if (!menu.hasClientData()) {
            return;
        }
        
        List<FamiliarMenu.SpellData> spells = menu.getSpellData();
        for (int i = 0; i < spells.size(); i++) {
            FamiliarMenu.SpellData spell = spells.get(i);
            int yOffset = SPELLS_START_Y + (i * SPELL_LINE_HEIGHT);

            SpellRemoveButton removeButton = new SpellRemoveButton(
                    this.leftPos + SPELL_DELETE_X, this.topPos + yOffset,
                    btn -> removeSpell(spell.name.getString())
            );
            addRenderableWidget(removeButton);
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        
        tickCounter++;
        if (tickCounter >= UPDATE_INTERVAL_TICKS) {
            tickCounter = 0;
            
            if (!minecraft.player.level().isClientSide() && !menu.stillValid(minecraft.player)) {
                this.onClose();
                return;
            }
            
            PacketHandler.network.sendToServer(new RequestFamiliarDataUpdate());
        }
    }

    private void removeSpell(String spellName) {
        PacketHandler.network.sendToServer(new RemoveFamiliarSpell(spellName));
        this.onClose();
    }

    private ItemStack getFrequencyIngot(FamiliarSpell.Frequency frequency) {
        return switch (frequency) {
            case IRON -> new ItemStack(Items.IRON_INGOT);
            case GOLD -> new ItemStack(Items.GOLD_INGOT);
            case COPPER -> new ItemStack(Items.COPPER_INGOT);
            case SUPERHEATED_PURIFIED_VINTEUM -> new ItemStack(ItemInit.VINTEUM_INGOT_PURIFIED_SUPERHEATED.get());
            case PURIFIED_VINTEUM -> new ItemStack(ItemInit.PURIFIED_VINTEUM_INGOT.get());
            case TRANSMUTED_SILVER -> new ItemStack(ItemInit.TRANSMUTED_SILVER.get());
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (!menu.hasClientData()) {
            return;
        }

        String familiarName = menu.getFamiliarName();
        if (familiarName != null && !familiarName.isEmpty()) {
            int nameWidth = this.font.width(familiarName);
            int centerX = (imageWidth - nameWidth) / 2;
            pGuiGraphics.drawString(this.font, familiarName, centerX, 7, Color.DARK_GRAY.getRGB(), false);
        }

        String kOwner = Component.translatable("gui.arcaneadditions.familiar.owner").getString() + ":";
        String vOwner = minecraft.player.getDisplayName().getString();
        pGuiGraphics.drawString(this.font, kOwner, INFO_LABEL_X, OWNER_Y, Color.DARK_GRAY.getRGB(), false);
        pGuiGraphics.drawString(this.font, vOwner, INFO_VALUE_X, OWNER_Y, Color.DARK_GRAY.getRGB(), false);

        String kType = Component.translatable("gui.arcaneadditions.familiar.type").getString() + ":";
        String vType = Component.translatable(menu.getFamiliarType()).getString();
        pGuiGraphics.drawString(this.font, kType, INFO_LABEL_X, TYPE_Y, Color.DARK_GRAY.getRGB(), false);
        pGuiGraphics.drawString(this.font, vType, INFO_VALUE_X, TYPE_Y, Color.DARK_GRAY.getRGB(), false);

        String kHealth = Component.translatable("gui.arcaneadditions.familiar.health").getString() + ":";
        String vHealth = String.format("%.0f/%.0f", menu.getFamiliarHealth(), menu.getFamiliarMaxHealth());
        pGuiGraphics.drawString(this.font, kHealth, INFO_LABEL_X, HEALTH_Y, Color.DARK_GRAY.getRGB(), false);
        pGuiGraphics.drawString(this.font, vHealth, INFO_VALUE_X, HEALTH_Y, Color.DARK_GRAY.getRGB(), false);

        String kMana = Component.translatable("gui.arcaneadditions.familiar.mana").getString() + ":";
        String vMana = String.format("%.0f/%.0f", menu.getMana(), menu.getMaxMana());
        pGuiGraphics.drawString(this.font, kMana, INFO_LABEL_X, MANA_Y, Color.DARK_GRAY.getRGB(), false);
        pGuiGraphics.drawString(this.font, vMana, INFO_VALUE_X, MANA_Y, Color.DARK_GRAY.getRGB(), false);

        List<FamiliarMenu.SpellData> spells = menu.getSpellData();

        String kSpellsKnown = Component.translatable("gui.arcaneadditions.familiar.spells_known").getString();
        pGuiGraphics.drawString(this.font, kSpellsKnown, INFO_LABEL_X, SPELLS_LABEL_Y, Color.DARK_GRAY.getRGB(), false);

        for (int i = 0; i < spells.size(); i++) {
            FamiliarMenu.SpellData spell = spells.get(i);
            int yOffset = SPELLS_START_Y + (i * SPELL_LINE_HEIGHT);

            int iconU = imageWidth + (spell.offensive ? 7 : 0);
            int iconV = 0;
            pGuiGraphics.blit(TEXTURE, SPELL_ICON_X, yOffset, iconU, iconV, 7, 7, 256, 256);

            String spellText = spell.name.getString() + " (" + (int)spell.manaCost + ")";
            pGuiGraphics.drawString(this.font, spellText, SPELL_TEXT_X, yOffset, Color.DARK_GRAY.getRGB(), false);

            ItemStack frequencyIngot = getFrequencyIngot(spell.frequency);
            if (!frequencyIngot.isEmpty()) {
                pGuiGraphics.renderItem(frequencyIngot, SPELL_INGOT_X, yOffset - 4);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float partialTicks, int mouse_x, int mouse_y) {
        this.renderBackground(pGuiGraphics);
        int xPos = this.leftPos;
        int yPos = this.topPos;
        pGuiGraphics.blit(TEXTURE, xPos, yPos, 0, 0, imageWidth, imageHeight, 256, 256);

        if (!menu.hasClientData()) {
            return;
        }

        try {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(menu.getFamiliarRegistryName()));
            if (entityType == null || minecraft.level == null) {
                return;
            }

            Mob displayEntity = (Mob) entityType.create(minecraft.level);
            if (displayEntity == null) {
                return;
            }

            if (menu.getFamiliarNbt() != null) {
                displayEntity.load(menu.getFamiliarNbt());
            }

            displayEntity.setCustomName(null);
            displayEntity.setCustomNameVisible(false);

            InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics,
                xPos + ENTITY_DISPLAY_X, yPos + ENTITY_DISPLAY_Y, ENTITY_SCALE,
                (float)(xPos + ENTITY_MOUSE_OFFSET_X) - mouse_x,
                (float)(yPos + ENTITY_DISPLAY_Y) - mouse_y,
                displayEntity);
        } catch (Exception e) {
        }
    }


    @Override
    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
        return pMouseX < pGuiLeft || pMouseY < pGuiTop || pMouseX >= pGuiLeft + this.imageWidth || pMouseY >= pGuiTop + this.imageHeight;
    }
    
    public void updateFamiliarData(float health, float maxHealth, float mana, float maxMana) {
        menu.updateClientData(health, maxHealth, mana, maxMana);
    }
}
