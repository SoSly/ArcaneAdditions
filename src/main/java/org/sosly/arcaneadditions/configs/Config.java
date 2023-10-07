/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.configs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class Config {

    private Config() {}

    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<Server, ForgeConfigSpec> serverSpec = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = serverSpec.getRight();
        SERVER = serverSpec.getLeft();
    }

    public static class Server {

        public final PolymorphConfig polymorph;
        public final SoulSearchersLensConfig soulSearchersLens;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server settings").push("server");

            polymorph = new PolymorphConfig(builder);
            soulSearchersLens = new SoulSearchersLensConfig(builder);

            builder.pop();

        }

        public static class PolymorphConfig {
            public final ForgeConfigSpec.BooleanValue allowSpellcasting;
            public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> tiers;

            public PolymorphConfig(ForgeConfigSpec.Builder builder) {
                builder.comment("Polymorph settings").push("polymorph");

                allowSpellcasting = builder.comment("If true, players will be able to cast spells while polymorphed.")
                        .translation("config.arcaneadditions.polymorph_allow_spellcasting_while_polymorphed")
                        .define("allowSpellcastingWhilePolymorphed", false);

                List<List<String>> morphTiers = Arrays.asList(
                        Arrays.asList("minecraft:cat", "minecraft_chicken", "minecraft:cow", "minecraft:donkey", "minecraft:fox", "minecraft:goat", "minecraft:horse", "minecraft:llama", "minecraft:mule", "minecraft:ocelot", "minecraft:panda", "minecraft:pig", "minecraft:rabbit", "minecraft:sheep", "minecraft:spider", "minecraft:wolf"),
                        Arrays.asList("minecraft:axolotl", "minecraft:bat", "minecraft:bee","minecraft:cave_spider","minecraft:cod","minecraft:dolphin","minecraft:mooshroom","minecraft:parrot","minecraft:pufferfish","minecraft:salmon","minecraft:polar_bear","minecraft:squid","minecraft:tropical_fish","minecraft:turtle"),
                        Arrays.asList("minecraft:creeper","minecraft:drowned","minecraft:glow_squid","minecraft:husk","minecraft:iron_golem","minecraft:piglin","minecraft:ravager","minecraft:silverfish","minecraft:skeleton","minecraft:skeleton_horse","minecraft:slime","minecraft:snow_golem","minecraft:stray","minecraft:strider","minecraft:zombie","minecraft:zombie_horse","minecraft:zombified_piglin"),
                        Arrays.asList("minecraft:blaze","minecraft:enderman","minecraft:endermite","minecraft:ghast","minecraft:giant","minecraft:guardian","minecraft:hoglin","minecraft:magma_cube","minecraft:phantom","minecraft:piglin_brute","minecraft:shulker","minecraft:vex","minecraft:wither_skeleton","minecraft:zoglin")
                );
                tiers = builder.comment("A list of polymorph tiers, each of which is a list of entity IDs and their corresponding polymorph spell IDs.")
                        .translation("config.arcaneadditions.polymorph_tiers")
                        .defineList("morphTiers", morphTiers, it -> it instanceof List);

                builder.pop();
            }
        }

        public static class SoulSearchersLensConfig {
            public final ForgeConfigSpec.IntValue healthPerLevel;
            public final ForgeConfigSpec.IntValue maxDistance;
            public final ForgeConfigSpec.ConfigValue<List<String>> creatureModifiers;

            public SoulSearchersLensConfig(ForgeConfigSpec.Builder builder) {
                builder.comment("Soul Searcher's Lens settings").push("soulsearchers_lens");

                creatureModifiers = builder.comment("An (optional) list of modifiers for specific creatures health values for determining the XP required to study that creature")
                        .translation("config.arcaneadditions.soulsearchers_lens_creature_modifiers")
                        .define("creatureModifiers", List.of("minecraft:villager,1.5"));
                healthPerLevel = builder.comment("For every multiple of this number that a creature has in max health, the player must spend a level to progress their phylactery progress.")
                        .translation("config.arcaneadditions.soulsearchers_lens_health_per_level")
                        .defineInRange("healthPerLevel", 20, 1, Integer.MAX_VALUE);
                maxDistance = builder.comment("This setting determines how many blocks away the player can be as they continue to study their target.")
                        .translation("config.arcaneadditions.soulsearchers_lens_max_distance")
                        .defineInRange("maxDistance", 5, 1, 32);

                builder.pop();
            }
        }
    }
}