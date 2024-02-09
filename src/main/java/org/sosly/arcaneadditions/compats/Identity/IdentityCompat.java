/*
 *   Arcane Additions Copyright (c) 2023, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats.Identity;

import com.mna.items.artifice.ItemThaumaturgicCompass;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.variant.IdentityType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.sosly.arcaneadditions.api.spells.components.IPolymorphProvider;
import org.sosly.arcaneadditions.compats.ICompat;

public class IdentityCompat implements ICompat, IPolymorphProvider {

    @Override
    public void setup() {}

    @Override
    public void polymorph(ServerPlayer target, LivingEntity creature) {
        IdentityType<?> defaultType = IdentityType.from(creature);
        if (defaultType != null) {
            boolean result = PlayerIdentity.updateIdentity(target, defaultType, creature);
            if (result && IdentityConfig.getInstance().logCommands()) {
                Component successMessage = Component.translatable("identity.equip_success", target.getDisplayName(), creature.getDisplayName());
                target.displayClientMessage(successMessage, true);
            }
        }
    }

    @Override
    public void unpolymorph(ServerPlayer target) {
        boolean result = PlayerIdentity.updateIdentity(target, null, null);
        if (result && IdentityConfig.getInstance().logCommands()) {
            Component successMessage = Component.translatable("identity.unequip_success", target.getDisplayName());
            target.displayClientMessage(successMessage, false);
        }
    }
}