package org.sosly.arcaneadditions.configs;

import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class FamiliarConfig {
    public final ForgeConfigSpec.ConfigValue<List<String>> familiars;

    public FamiliarConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Familiar settings").push("familiars");

        familiars = builder.comment("A list of entity types that can be bound as familiars.")
                .translation("config.arcaneadditions.familiars")
                .define("familiars", List.of(
                        "minecraft:bat", "minecraft:cat", "minecraft:parrot", "minecraft:fox",
                        "minecraft:rabbit", "minecraft:chicken"
                ));

        builder.pop();
    }
}
