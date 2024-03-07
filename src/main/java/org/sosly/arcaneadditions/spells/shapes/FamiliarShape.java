package org.sosly.arcaneadditions.spells.shapes;

import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.base.ISpellDefinition;
import com.mna.api.spells.parts.Shape;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.List;

public class FamiliarShape extends Shape {
    public FamiliarShape(ResourceLocation icon) {
        super(icon);
    }

    public List<SpellTarget> Target(SpellSource source, Level level, IModifiedSpellPart<Shape> modifiedData, ISpellDefinition recipe) {
        if (source.getCaster() == null || !(source.getCaster() instanceof Player)) {
            return null;
        }

        Player player = (Player) source.getCaster();
        if (!FamiliarHelper.hasFamiliar(player)) {
            return null;
        }

        Mob familiar = FamiliarHelper.getFamiliar(player);
        if (familiar == null) {
            return null;
        }

        return List.of(new SpellTarget(familiar));
    }

    public float initialComplexity() {
        return 5.0F;
    }

    public int requiredXPForRote() {
        return 100;
    }

    public SpellPartTags getUseTag() {
        return SpellPartTags.FRIENDLY;
    }

    public boolean affectsCaster() {
        return false;
    }
}
