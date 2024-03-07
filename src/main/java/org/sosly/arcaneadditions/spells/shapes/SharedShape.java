package org.sosly.arcaneadditions.spells.shapes;

import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.base.ISpellDefinition;
import com.mna.api.spells.parts.Shape;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.capabilities.familiar.FamiliarProvider;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;

import java.util.List;

public class SharedShape extends Shape {
    public SharedShape(ResourceLocation icon) {
        super(icon);
    }

    public List<SpellTarget> Target(SpellSource source, Level level, IModifiedSpellPart<Shape> modifiedData, ISpellDefinition recipe) {
        if (source.getCaster() == null) {
            return null;
        }

        IFamiliarCapability cap = source.getCaster().getCapability(FamiliarProvider.FAMILIAR).orElse(null);
        if (cap == null || cap.getFamiliar(level) == null) {
            return null;
        }

        TamableAnimal familiar = cap.getFamiliar(level).get();
        if (familiar == null) {
            return null;
        }

        Entity entity = level.getEntity(familiar.getId());
        if (entity == null) {
            return null;
        }

        return List.of(new SpellTarget(entity), new SpellTarget(source.getCaster()));
    }

    public float initialComplexity() {
        return 10.0F;
    }

    public int requiredXPForRote() {
        return 100;
    }

    public SpellPartTags getUseTag() {
        return SpellPartTags.FRIENDLY;
    }

    public boolean affectsCaster() {
        return true;
    }
}
