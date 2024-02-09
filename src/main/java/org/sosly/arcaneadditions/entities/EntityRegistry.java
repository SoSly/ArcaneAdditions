/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.entities.sorcery.AstralProjectionEntity;
import org.sosly.arcaneadditions.entities.sorcery.IceBlockEntity;
import org.sosly.arcaneadditions.entities.sorcery.SoulSearchersBeamEntity;

@Mod.EventBusSubscriber(bus = Bus.MOD, modid = ArcaneAdditions.MOD_ID)
public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID);
    public static final RegistryObject<EntityType<AstralProjectionEntity>> ASTRAL_PROJECTION = ENTITY_TYPES.register("astral_projection", () ->
            EntityType.Builder.<AstralProjectionEntity>of(AstralProjectionEntity::new, MobCategory.MISC)
                    .noSummon()
                    .sized(1.0f, 1.8f)
                    .build("arcaneadditions:astral_projection"));
    public static final RegistryObject<EntityType<IceBlockEntity>> ICE_BLOCK = ENTITY_TYPES.register("ice_block", () ->
            EntityType.Builder.of(IceBlockEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .build("arcaneadditions:ice_block"));
    public static final RegistryObject<EntityType<SoulSearchersBeamEntity>> SOUL_SEARCHERS_BEAM = ENTITY_TYPES.register("soul_searchers_beam", () ->
            EntityType.Builder.of(SoulSearchersBeamEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build("arcaneadditions:soul_searchers_beam"));

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ASTRAL_PROJECTION.get(), AstralProjectionEntity.getGlobalAttributes().build());
    }
}
