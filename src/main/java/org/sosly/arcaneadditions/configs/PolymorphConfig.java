/*
 *   Arcane Additions Copyright (c) 2023, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.configs;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PolymorphConfig {
    public final ForgeConfigSpec.BooleanValue allowSpellcasting;
    public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> tiers;

    public PolymorphConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Polymorph settings").push("polymorph");

        allowSpellcasting = builder.comment("If true, players will be able to cast spells while polymorphed.")
                .translation("config.arcaneadditions.polymorph_allow_spellcasting_while_polymorphed")
                .define("allowSpellcastingWhilePolymorphed", false);

        // Minecraft
        ArrayList<ArrayList<String>> minecraft = getMinecraft();
        ArrayList<String> tier1polymorphs = minecraft.get(0);
        ArrayList<String> tier2polymorphs = minecraft.get(1);
        ArrayList<String> tier3polymorphs = minecraft.get(2);
        ArrayList<String> tier4polymorphs = minecraft.get(3);

        // Alex's mobs
        if (ModList.get().isLoaded("alexsmobs")) {
            ArrayList<ArrayList<String>> alexsMobs = getAlexsMobs();
            tier1polymorphs.addAll(alexsMobs.get(0));
            tier2polymorphs.addAll(alexsMobs.get(1));
            tier3polymorphs.addAll(alexsMobs.get(2));
            tier4polymorphs.addAll(alexsMobs.get(3));
        }

        // critters and companions
        if (ModList.get().isLoaded("crittersandcompanions")) {
            ArrayList<ArrayList<String>> crittersAndCompanions = getCrittersAndCompanions();
            tier1polymorphs.addAll(crittersAndCompanions.get(0));
            tier2polymorphs.addAll(crittersAndCompanions.get(1));
            tier3polymorphs.addAll(crittersAndCompanions.get(2));
            tier4polymorphs.addAll(crittersAndCompanions.get(3));
        }

        // lil wings
        if (ModList.get().isLoaded("lilwings")) {
            ArrayList<ArrayList<String>> lilWings = getLilWings();
            tier1polymorphs.addAll(lilWings.get(0));
            tier2polymorphs.addAll(lilWings.get(1));
            tier3polymorphs.addAll(lilWings.get(2));
            tier4polymorphs.addAll(lilWings.get(3));
        }

        List<List<String>> morphTiers = Arrays.asList(tier1polymorphs, tier2polymorphs, tier3polymorphs, tier4polymorphs);

        tiers = builder.comment("A list of polymorph tiers, each of which is a list of entity IDs and their corresponding polymorph spell IDs.")
                .translation("config.arcaneadditions.polymorph_tiers")
                .defineList("morphTiers", morphTiers, it -> it instanceof List);

        builder.pop();
    }

    private static ArrayList<ArrayList<String>> getAlexsMobs() {
        ArrayList<ArrayList<String>> polymorphs = new ArrayList<>();

        ArrayList<String> tier1polymorphs = new ArrayList<>();
        ArrayList<String> tier2polymorphs = new ArrayList<>();
        ArrayList<String> tier3polymorphs = new ArrayList<>();
        ArrayList<String> tier4polymorphs = new ArrayList<>();

        tier1polymorphs.add("alexsmobs:anteater");
        tier1polymorphs.add("alexsmobs:bunfungus");
        tier1polymorphs.add("alexsmobs:capuchin_monkey");
        tier1polymorphs.add("alexsmobs:cockroach");
        tier1polymorphs.add("alexsmobs:emu");
        tier1polymorphs.add("alexsmobs:gazelle");
        tier1polymorphs.add("alexsmobs:gelada_monkey");
        tier1polymorphs.add("alexsmobs:jerboa");
        tier1polymorphs.add("alexsmobs:leafcutter_ant");
        tier1polymorphs.add("alexsmobs:maned_wolf");
        tier1polymorphs.add("alexsmobs:raccoon");
        tier1polymorphs.add("alexsmobs:rattlesnake");
        tier1polymorphs.add("alexsmobs:roadrunner");
        tier1polymorphs.add("alexsmobs:seal");
        tier1polymorphs.add("alexsmobs:tasmanian_devil");

        tier2polymorphs.add("alexsmobs:alligator_snapping_turtle");
        tier2polymorphs.add("alexsmobs:anaconda");
        tier2polymorphs.add("alexsmobs:bald_eagle");
        tier2polymorphs.add("alexsmobs:bison");
        tier2polymorphs.add("alexsmobs:blobfish");
        tier2polymorphs.add("alexsmobs:catfish");
        tier2polymorphs.add("alexsmobs:comb_jelly");
        tier2polymorphs.add("alexsmobs:crocodile");
        tier2polymorphs.add("alexsmobs:devils_hole_pupfish");
        tier2polymorphs.add("alexsmobs:elephant");
        tier2polymorphs.add("alexsmobs:flutter");
        tier2polymorphs.add("alexsmobs:fly");
        tier2polymorphs.add("alexsmobs:frilled_shark");
        tier2polymorphs.add("alexsmobs:froststalker");
        tier2polymorphs.add("alexsmobs:giant_squid");
        tier2polymorphs.add("alexsmobs:gorilla");
        tier2polymorphs.add("alexsmobs:grizzly_bear");
        tier2polymorphs.add("alexsmobs:hummingbird");
        tier2polymorphs.add("alexsmobs:kangaroo");
        tier2polymorphs.add("alexsmobs:komodo_dragon");
        tier2polymorphs.add("alexsmobs:lobster");
        tier2polymorphs.add("alexsmobs:mantis_shrimp");
        tier2polymorphs.add("alexsmobs:moose");
        tier2polymorphs.add("alexsmobs:platypus");
        tier2polymorphs.add("alexsmobs:rhinoceros");
        tier2polymorphs.add("alexsmobs:rocky_roller");
        tier2polymorphs.add("alexsmobs:seagull");
        tier2polymorphs.add("alexsmobs:shoebill");
        tier2polymorphs.add("alexsmobs:snow_leopard");
        tier2polymorphs.add("alexsmobs:terrapin");
        tier2polymorphs.add("alexsmobs:tiger");
        tier2polymorphs.add("alexsmobs:toucan");
        tier2polymorphs.add("alexsmobs:tusklin");
        tier2polymorphs.add("alexsmobs:warped_toad");

        tier3polymorphs.add("alexsmobs:cachalot_whale");
        tier3polymorphs.add("alexsmobs:cosmic_cod");
        tier3polymorphs.add("alexsmobs:crimson_mosquito");
        tier3polymorphs.add("alexsmobs:dropbear");
        tier3polymorphs.add("alexsmobs:endergrade");
        tier3polymorphs.add("alexsmobs:guster");
        tier3polymorphs.add("alexsmobs:hammerhead_shark");
        tier3polymorphs.add("alexsmobs:mimicube");
        tier3polymorphs.add("alexsmobs:orca");
        tier3polymorphs.add("alexsmobs:skelewag");
        tier3polymorphs.add("alexsmobs:soul_vulture");
        tier3polymorphs.add("alexsmobs:tarantula_hawk");

        tier4polymorphs.add("alexsmobs:cosmaw");
        tier4polymorphs.add("alexsmobs:enderiophage");
        tier4polymorphs.add("alexsmobs:laviathan");
        tier4polymorphs.add("alexsmobs:spectre");
        tier4polymorphs.add("alexsmobs:straddler");
        tier4polymorphs.add("alexsmobs:stradpole");
        tier4polymorphs.add("alexsmobs:sunbird");
        tier4polymorphs.add("alexsmobs:warped_mosco");

        polymorphs.add(tier1polymorphs);
        polymorphs.add(tier2polymorphs);
        polymorphs.add(tier3polymorphs);
        polymorphs.add(tier4polymorphs);

        return polymorphs;
    }

    private static ArrayList<ArrayList<String>> getCrittersAndCompanions() {
        ArrayList<ArrayList<String>> polymorphs = new ArrayList<>();

        ArrayList<String> tier1polymorphs = new ArrayList<>();
        ArrayList<String> tier2polymorphs = new ArrayList<>();
        ArrayList<String> tier3polymorphs = new ArrayList<>();
        ArrayList<String> tier4polymorphs = new ArrayList<>();

        tier1polymorphs.add("crittersandcompanions:ferret");
        tier1polymorphs.add("crittersandcompanions:leaf_insect");
        tier1polymorphs.add("crittersandcompanions:otter");
        tier1polymorphs.add("crittersandcompanions:red_panda");

        tier2polymorphs.add("crittersandcompanions:dragonfly");
        tier2polymorphs.add("crittersandcompanions:dumbo_octopus");
        tier2polymorphs.add("crittersandcompanions:koi_fish");
        tier1polymorphs.add("crittersandcompanions:sea_bunny");

        polymorphs.add(tier1polymorphs);
        polymorphs.add(tier2polymorphs);
        polymorphs.add(tier3polymorphs);
        polymorphs.add(tier4polymorphs);

        return polymorphs;
    }

    private static ArrayList<ArrayList<String>> getLilWings() {
        ArrayList<ArrayList<String>> polymorphs = new ArrayList<>();
        ArrayList<String> tier1polymorphs = new ArrayList<>();
        ArrayList<String> tier2polymorphs = new ArrayList<>();
        ArrayList<String> tier3polymorphs = new ArrayList<>();
        ArrayList<String> tier4polymorphs = new ArrayList<>();

        tier2polymorphs.add("lilwings:aponi_butterfly");
        tier2polymorphs.add("lilwings:butter_gold_butterfly");
        tier2polymorphs.add("lilwings:cloudy_puff_butterfly");
        tier2polymorphs.add("lilwings:crystal_puff_butterfly");
        tier2polymorphs.add("lilwings:gold_applefly_butterfly");
        tier2polymorphs.add("lilwings:grayling_butterfly");
        tier2polymorphs.add("lilwings:painted_panther_butterfly");
        tier2polymorphs.add("lilwings:red_applefly_butterfly");
        tier2polymorphs.add("lilwings:shroom_skipper_butterfly");
        tier2polymorphs.add("lilwings:swallow_tail_butterfly");
        tier2polymorphs.add("lilwings:swamp_hopper_butterfly");
        tier2polymorphs.add("lilwings:white_fox_butterfly");

        polymorphs.add(tier1polymorphs);
        polymorphs.add(tier2polymorphs);
        polymorphs.add(tier3polymorphs);
        polymorphs.add(tier4polymorphs);

        return polymorphs;
    }

    private static ArrayList<ArrayList<String>> getMinecraft() {
        ArrayList<ArrayList<String>> polymorphs = new ArrayList<>();

        ArrayList<String> tier1polymorphs = new ArrayList<>();
        ArrayList<String> tier2polymorphs = new ArrayList<>();
        ArrayList<String> tier3polymorphs = new ArrayList<>();
        ArrayList<String> tier4polymorphs = new ArrayList<>();

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

        polymorphs.add(tier1polymorphs);
        polymorphs.add(tier2polymorphs);
        polymorphs.add(tier3polymorphs);
        polymorphs.add(tier4polymorphs);

        return polymorphs;
    }
}