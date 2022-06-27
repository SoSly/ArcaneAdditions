/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.arcaneadditions.entities.sorcery.SoulSearchersBeamEntity;
import org.sosly.arcaneadditions.entities.sorcery.IceBlockEntity;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID);
    public static final RegistryObject<EntityType<IceBlockEntity>> ICE_BLOCK = ENTITY_TYPES.register("ice_block", () ->
            EntityType.Builder.of(IceBlockEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .build("arcaneadditions:ice_block"));
    public static final RegistryObject<EntityType<SoulSearchersBeamEntity>> SOUL_SEARCHERS_BEAM = ENTITY_TYPES.register("soul_searchers_beam", () ->
            EntityType.Builder.of(SoulSearchersBeamEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build("arcaneadditions:soul_searchers_beam"));
}
