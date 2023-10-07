/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.configs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

                ArrayList<String> tier1polymorphs = new ArrayList<String>();
                tier1polymorphs.add("minecraft:cat");
                tier1polymorphs.add("minecraft:chicken");
                tier1polymorphs.add("minecraft:cow");
                tier1polymorphs.add("minecraft:donkey");
                tier1polymorphs.add("minecraft:fox");
                tier1polymorphs.add("minecraft:goat");
                tier1polymorphs.add("minecraft:horse");
                tier1polymorphs.add("minecraft:llama");
                tier1polymorphs.add("minecraft:mule");
                tier1polymorphs.add("minecraft:ocelot");
                tier1polymorphs.add("minecraft:panda");
                tier1polymorphs.add("minecraft:pig");
                tier1polymorphs.add("minecraft:rabbit");
                tier1polymorphs.add("minecraft:sheep");
                tier1polymorphs.add("minecraft:spider");
                tier1polymorphs.add("minecraft:wolf");

                ArrayList<String> tier2polymorphs = new ArrayList<String>();
                tier2polymorphs.add("minecraft:axolotl");
                tier2polymorphs.add("minecraft:bat");
                tier2polymorphs.add("minecraft:bee");
                tier2polymorphs.add("minecraft:cave_spider");
                tier2polymorphs.add("minecraft:cod");
                tier2polymorphs.add("minecraft:dolphin");
                tier2polymorphs.add("minecraft:mooshroom");
                tier2polymorphs.add("minecraft:parrot");
                tier2polymorphs.add("minecraft:pufferfish");
                tier2polymorphs.add("minecraft:salmon");
                tier2polymorphs.add("minecraft:polar_bear");
                tier2polymorphs.add("minecraft:squid");
                tier2polymorphs.add("minecraft:tropical_fish");
                tier2polymorphs.add("minecraft:turtle");

                ArrayList<String> tier3polymorphs = new ArrayList<String>();
                tier3polymorphs.add("minecraft:creeper");
                tier3polymorphs.add("minecraft:drowned");
                tier3polymorphs.add("minecraft:glow_squid");
                tier3polymorphs.add("minecraft:husk");
                tier3polymorphs.add("minecraft:iron_golem");
                tier3polymorphs.add("minecraft:piglin");
                tier3polymorphs.add("minecraft:ravager");
                tier3polymorphs.add("minecraft:silverfish");
                tier3polymorphs.add("minecraft:skeleton");
                tier3polymorphs.add("minecraft:skeleton_horse");
                tier3polymorphs.add("minecraft:slime");
                tier3polymorphs.add("minecraft:snow_golem");
                tier3polymorphs.add("minecraft:stray");
                tier3polymorphs.add("minecraft:strider");
                tier3polymorphs.add("minecraft:zombie");
                tier3polymorphs.add("minecraft:zombie_horse");
                tier3polymorphs.add("minecraft:zombified_piglin");

                ArrayList<String> tier4polymorphs = new ArrayList<String>();
                tier4polymorphs.add("minecraft:blaze");
                tier4polymorphs.add("minecraft:enderman");
                tier4polymorphs.add("minecraft:endermite");
                tier4polymorphs.add("minecraft:ghast");
                tier4polymorphs.add("minecraft:giant");
                tier4polymorphs.add("minecraft:guardian");
                tier4polymorphs.add("minecraft:hoglin");
                tier4polymorphs.add("minecraft:magma_cube");
                tier4polymorphs.add("minecraft:phantom");
                tier4polymorphs.add("minecraft:piglin_brute");
                tier4polymorphs.add("minecraft:shulker");
                tier4polymorphs.add("minecraft:vex");
                tier4polymorphs.add("minecraft:wither_skeleton");
                tier4polymorphs.add("minecraft:zoglin");

                // critters and companions
                if (ModList.get().isLoaded("crittersandcompanions")) {
                    tier1polymorphs.add("crittersandcompanions:ferret");
                    tier1polymorphs.add("crittersandcompanions:leaf_insect");
                    tier1polymorphs.add("crittersandcompanions:otter");
                    tier1polymorphs.add("crittersandcompanions:red_panda");

                    tier2polymorphs.add("crittersandcompanions:dragonfly");
                    tier2polymorphs.add("crittersandcompanions:dumbo_octopus");
                    tier2polymorphs.add("crittersandcompanions:koi_fish");
                    tier1polymorphs.add("crittersandcompanions:sea_bunny");
                }

                List<List<String>> morphTiers = Arrays.asList(tier1polymorphs, tier2polymorphs, tier3polymorphs, tier4polymorphs);

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