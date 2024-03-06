package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.faction.IFaction;
import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.attributes.AttributeValuePair;
import com.mna.factions.Factions;
import com.mna.spells.components.PotionEffectComponent;
import net.minecraft.resources.ResourceLocation;
import org.sosly.arcaneadditions.effects.EffectRegistry;

public class CounterspellComponent extends PotionEffectComponent {
    public CounterspellComponent(ResourceLocation guiIcon) {
        super(guiIcon, EffectRegistry.ANTI_MAGIC,
                new AttributeValuePair(Attribute.MAGNITUDE, 1.0F, 1.0F, 5.0F, 1.0F, 5.0F),
                new AttributeValuePair(Attribute.DURATION, 2.0F, 2.0F, 30.0F, 2.0F, 1.0F));
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
    public IFaction getFactionRequirement() {
        return Factions.COUNCIL;
    }
}
