package org.sosly.arcaneadditions.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class FamiliarConfig {
    public final ForgeConfigSpec.ConfigValue<List<String>> familiars;

    public FamiliarConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Familiar settings").push("familiars");

        familiars = builder.comment("A list of entity types that can be bound as familiars.")
                .translation("config.arcaneadditions.familiars")
                .define("familiars", List.of(
                        "minecraft:cat", "minecraft:parrot", "minecraft:wolf",
                        "minecraft:ocelot", "minecraft:fox", "minecraft:rabbit",
                        "minecraft:chicken", "minecraft:cow", "minecraft:pig",
                        "minecraft:sheep"
                ));

        builder.pop();
    }
}
