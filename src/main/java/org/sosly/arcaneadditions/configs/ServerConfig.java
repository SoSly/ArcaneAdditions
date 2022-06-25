/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.configs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class ServerConfig {
    public static ForgeConfigSpec CONFIG;

    public static final ForgeConfigSpec.ConfigValue<Integer> SOULSEARCHERS_LENS_HEALTH_PER_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> SOULSEARCHERS_LENS_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> SOULSEARCHERS_LENS_CREATURE_MODIFIERS;

    public static final ForgeConfigSpec.BooleanValue ALLOW_SPELLCASTING_WHILE_POLYMORPHED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> POLYMORPH_TIERS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("arcaneadditions:items/soulsearchers_lens");
        SOULSEARCHERS_LENS_CREATURE_MODIFIERS = builder.comment("An (optional) list of modifiers for specific creatures heawlth values for determinign the XP required to study that creature")
            .translation("config.arcaneadditions.soulsearchers_lens_creature_modifiers")
            .define("creatureModifiers", Arrays.asList("minecraft:villager,1.5"));
        SOULSEARCHERS_LENS_HEALTH_PER_LEVEL = builder.comment("For every multiple of this number that a creature has in max health, the player must spend a level to progress their phylactery progress.")
            .translation("config.arcaneadditions.soulsearchers_lens_health_per_level")
            .define("healthPerLevel", 10);
        SOULSEARCHERS_LENS_MAX_DISTANCE = builder.comment("This setting determines how many blocks away the player can be as they continue to study their target.")
            .translation("config.arcaneadditions.soulsearchers_lens_max_distance")
            .define("maxDistance", 5);
        builder.pop();

        builder.push("arcaneadditions:components/polymorph");
        ALLOW_SPELLCASTING_WHILE_POLYMORPHED = builder.comment("Disabling this setting prevents players from casting spells while polymorphed")
            .translation("config.arcaneadditions.allow_spellcasting_while_polymorphed")
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
        ServerConfig.CONFIG = builder.build();
    }
}