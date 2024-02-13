/*
 *   Arcane Additions Copyright (c) 2023, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats.Woodwalkers;

import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.ShapeType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.sosly.arcaneadditions.api.spells.components.IPolymorphProvider;
import org.sosly.arcaneadditions.compats.ICompat;

public class WoodwalkersCompat implements ICompat, IPolymorphProvider {

    @Override
    public void setup() {}

    @Override
    public void polymorph(ServerPlayer target, LivingEntity creature) {
        ShapeType<?> defaultType = ShapeType.from(creature);
        if (defaultType != null) {
            boolean result = PlayerShape.updateShapes(target, creature);
            if (result) {
                Component successMessage = Component.translatable("effect.arcaneadditions.polymorph.become", target.getDisplayName(), creature.getDisplayName());
                target.displayClientMessage(successMessage, true);
            }
        }
    }

    @Override
    public void unpolymorph(ServerPlayer target) {
        boolean result = PlayerShape.updateShapes(target, null);
        if (result) {
            Component successMessage = Component.translatable("effect.arcaneadditions.polymorph.revert", target.getDisplayName());
            target.displayClientMessage(successMessage, false);
        }
    }
}