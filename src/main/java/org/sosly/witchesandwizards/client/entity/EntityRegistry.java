package org.sosly.witchesandwizards.client.entity;

import com.mna.entities.EntityClientInit;
import com.mna.entities.EntityInit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.witchesandwizards.WitchesAndWizards;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, WitchesAndWizards.MOD_ID);

    public static final RegistryObject<EntityType<IceBlockEntity>> ICE_BLOCK = ENTITY_TYPES.register("ice_block", () -> {
        return EntityType.Builder.of(IceBlockEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).setShouldReceiveVelocityUpdates(false).fireImmune().build("wnw:ice_block");
    });
}
