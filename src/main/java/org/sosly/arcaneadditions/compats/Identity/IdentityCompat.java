/*
 *   Arcane Additions Copyright (c) 2023, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats.Identity;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.variant.IdentityType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.sosly.arcaneadditions.api.spells.components.IPolymorphProvider;
import org.sosly.arcaneadditions.compats.ICompat;

public class IdentityCompat implements ICompat, IPolymorphProvider {

    @Override
    public void setup() {}

    @Override
    public void polymorph(ServerPlayer target, ResourceLocation creature) {
        EntityType<?> entity = Registry.ENTITY_TYPE.get(creature);
        Entity createdEntity = entity.create(target.level);
        if (createdEntity instanceof LivingEntity living) {
            IdentityType<?> defaultType = IdentityType.from(living);
            if (defaultType != null) {
                boolean result = PlayerIdentity.updateIdentity(target, defaultType, (LivingEntity)createdEntity);
                if (result && IdentityConfig.getInstance().logCommands()) {
                    TranslatableComponent successMessage = new TranslatableComponent("identity.equip_success",
                        new Object[]{
                            new TranslatableComponent(entity.getDescriptionId()),
                            target.getDisplayName()
                        });
                    target.displayClientMessage(successMessage, true);
                }
            }
        }
    }

    @Override
    public void unpolymorph(ServerPlayer target) {
        boolean result = PlayerIdentity.updateIdentity(target, null, null);
        if (result && IdentityConfig.getInstance().logCommands()) {
            TranslatableComponent successMessage = new TranslatableComponent("identity.unequip_success",
                    new Object[]{
                            target.getDisplayName()
            });
            target.displayClientMessage(successMessage, false);
        }
    }
}