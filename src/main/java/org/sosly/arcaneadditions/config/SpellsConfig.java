package org.sosly.arcaneadditions.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class SpellsConfig {
    public static ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.BooleanValue ALLOW_SPELLCASTING_WHILE_POLYMORPHED;
    public static ForgeConfigSpec.ConfigValue<List<? extends List<String>>> POLYMORPH_TIERS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("arcaneadditions:components/polymorph");
        ALLOW_SPELLCASTING_WHILE_POLYMORPHED = builder.comment("Disabling this setting prevents players from casting spells while polymorphed")
            .translation("arcaneadditions.spells-config.allow_spellcasting_while_polymorphed")
            .define("allowSpellcasting", true);

        List<List<String>> morphTiers = Arrays.asList(
                Arrays.asList("minecraft:cat", "minecraft_chicken", "minecraft:cow", "minecraft:donkey", "minecraft:fox", "minecraft:goat", "minecraft:horse", "minecraft:llama", "minecraft:mule", "minecraft:ocelot", "minecraft:panda", "minecraft:pig", "minecraft:rabbit", "minecraft:sheep", "minecraft:spider", "minecraft:wolf"),
                Arrays.asList("minecraft:axolotl", "minecraft:bat", "minecraft:bee","minecraft:cave_spider","minecraft:cod","minecraft:dolphin","minecraft:mooshroom","minecraft:parrot","minecraft:pufferfish","minecraft:salmon","minecraft:polar_bear","minecraft:squid","minecraft:tropical_fish","minecraft:turtle"),
                Arrays.asList("minecraft:creeper","minecraft:drowned","minecraft:glow_squid","minecraft:husk","minecraft:iron_golem","minecraft:piglin","minecraft:ravager","minecraft:silverfish","minecraft:skeleton","minecraft:skeleton_horse","minecraft:slime","minecraft:snow_golem","minecraft:stray","minecraft:strider","minecraft:zombie","minecraft:zombie_horse","minecraft:zombified_piglin"),
                Arrays.asList("minecraft:blaze","minecraft:enderman","minecraft:endermite","minecraft:ghast","minecraft:giant","minecraft:guardian","minecraft:hoglin","minecraft:magma_cube","minecraft:phantom","minecraft:piglin_brute","minecraft:shulker","minecraft:vex","minecraft:wither_skeleton","minecraft:zoglin")
        );

        POLYMORPH_TIERS = builder.comment("The list of morph registries associated with each magnitude of polymorph")
            .translation("arcaneadditions.spells-config.polymorph-magnitude-lists")
            .defineList("morphTiers", morphTiers, it -> it instanceof List);

        builder.pop();
        SpellsConfig.CONFIG = builder.build();
    }
}