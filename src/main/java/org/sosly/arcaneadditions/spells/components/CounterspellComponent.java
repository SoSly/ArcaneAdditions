/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.capabilities.Faction;
import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.attributes.AttributeValuePair;
import com.mna.spells.components.PotionEffectComponent;
import net.minecraft.resources.ResourceLocation;
import org.sosly.arcaneadditions.effects.EffectRegistry;

public class CounterspellComponent extends PotionEffectComponent {
    public CounterspellComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon, EffectRegistry.ANTI_MAGIC,
                new AttributeValuePair(Attribute.MAGNITUDE, 1.0F, 1.0F, 5.0F, 1.0F, 5.0F),
                new AttributeValuePair(Attribute.DURATION, 10.0F, 10.0F, 30.0F, 5.0F, 1.0F));
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.ARCANE;
    }

    @Override
    public float initialComplexity() {
        return 25;
    }

    @Override
    public int requiredXPForRote() {
        return 100;
    }

    @Override
    public SpellPartTags getUseTag() {
        return SpellPartTags.UTILITY;
    }

    @Override
    public boolean targetsBlocks() {
        return false;
    }

    @Override
    public Faction getFactionRequirement() {
        return Faction.ANCIENT_WIZARDS;
    }
}
