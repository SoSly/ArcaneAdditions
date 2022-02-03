package org.sosly.arcaneadditions.client.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID);

    public static final RegistryObject<EntityType<IceBlockEntity>> ICE_BLOCK = ENTITY_TYPES.register("ice_block", () -> EntityType.Builder.of(IceBlockEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).setShouldReceiveVelocityUpdates(false).fireImmune().build("arcaneadditions:ice_block"));
}
