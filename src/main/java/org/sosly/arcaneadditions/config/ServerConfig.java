/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.sosly.arcaneadditions.ArcaneAdditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Server-side configuration for Arcane Additions mod
// These settings are stored per-world and synced from server to clients
@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Familiar Settings
    static {
        BUILDER.push("familiars");
        BUILDER.comment("Familiar settings");
    }

    private static final ForgeConfigSpec.ConfigValue<List<String>> FAMILIARS = BUILDER
            .comment("A list of entity types that can be bound as familiars.")
            .translation("config.arcaneadditions.familiars")
            .define("familiars", List.of(
                    "minecraft:bat", "minecraft:cat", "minecraft:parrot", "minecraft:fox",
                    "minecraft:rabbit", "minecraft:chicken", "minecraft:allay"
            ), ServerConfig::isValidEntityList);

    static {
        BUILDER.pop();
    }

    // Polymorph Settings
    static {
        BUILDER.push("polymorph");
        BUILDER.comment("Polymorph settings");
    }

    private static final ForgeConfigSpec.BooleanValue POLYMORPH_ALLOW_SPELLCASTING = BUILDER
            .comment("If true, players will be able to cast spells while polymorphed.")
            .translation("config.arcaneadditions.polymorph_allow_spellcasting_while_polymorphed")
            .define("allowSpellcastingWhilePolymorphed", false);

    private static final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> POLYMORPH_TIERS = BUILDER
            .comment("A list of polymorph tiers, each of which is a list of entity IDs and their corresponding polymorph spell IDs.")
            .translation("config.arcaneadditions.polymorph_tiers")
            .defineList("morphTiers", getDefaultPolymorphTiers(), it -> it instanceof List);

    static {
        BUILDER.pop();
    }

    // Soul Searcher's Lens Settings
    static {
        BUILDER.push("soulsearchers_lens");
        BUILDER.comment("Soul Searcher's Lens settings");
    }

    private static final ForgeConfigSpec.ConfigValue<List<String>> SOUL_SEARCHERS_LENS_CREATURE_MODIFIERS = BUILDER
            .comment("An (optional) list of modifiers for specific creatures health values for determining the XP required to study that creature")
            .translation("config.arcaneadditions.soulsearchers_lens_creature_modifiers")
            .define("creatureModifiers", List.of("minecraft:villager,1.5"));

    private static final ForgeConfigSpec.IntValue SOUL_SEARCHERS_LENS_HEALTH_PER_LEVEL = BUILDER
            .comment("For every multiple of this number that a creature has in max health, the player must spend a level to progress their phylactery progress.")
            .translation("config.arcaneadditions.soulsearchers_lens_health_per_level")
            .defineInRange("healthPerLevel", 20, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue SOUL_SEARCHERS_LENS_MAX_DISTANCE = BUILDER
            .comment("This setting determines how many blocks away the player can be as they continue to study their target.")
            .translation("config.arcaneadditions.soulsearchers_lens_max_distance")
            .defineInRange("maxDistance", 5, 1, 32);

    static {
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // Runtime cached values
    public static List<String> familiars;
    public static boolean polymorphAllowSpellcasting;
    public static List<? extends List<String>> polymorphTiers;
    public static List<String> soulSearchersLensCreatureModifiers;
    public static int soulSearchersLensHealthPerLevel;
    public static int soulSearchersLensMaxDistance;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        familiars = FAMILIARS.get();
        polymorphAllowSpellcasting = POLYMORPH_ALLOW_SPELLCASTING.get();
        polymorphTiers = POLYMORPH_TIERS.get();
        soulSearchersLensCreatureModifiers = SOUL_SEARCHERS_LENS_CREATURE_MODIFIERS.get();
        soulSearchersLensHealthPerLevel = SOUL_SEARCHERS_LENS_HEALTH_PER_LEVEL.get();
        soulSearchersLensMaxDistance = SOUL_SEARCHERS_LENS_MAX_DISTANCE.get();
    }

    public static <T> boolean isValidEntityList(T entry) {
        if (!(entry instanceof List)) {
            return false;
        }

        for (Object i : (List<?>) entry) {
            if (!(i instanceof String)) {
                return false;
            }

            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation((String) i));
            if (type == null) {
                return false;
            }
        }

        return true;
    }

    private static List<List<String>> getDefaultPolymorphTiers() {
        ArrayList<String> tier1polymorphs = new ArrayList<>();
        ArrayList<String> tier2polymorphs = new ArrayList<>();
        ArrayList<String> tier3polymorphs = new ArrayList<>();
        ArrayList<String> tier4polymorphs = new ArrayList<>();

        tier1polymorphs.addAll(Arrays.asList(
                "minecraft:cat", "minecraft:chicken", "minecraft:cow", "minecraft:donkey", "minecraft:fox",
                "minecraft:goat", "minecraft:horse", "minecraft:llama", "minecraft:mule", "minecraft:ocelot",
                "minecraft:panda", "minecraft:pig", "minecraft:rabbit", "minecraft:sheep", "minecraft:spider", "minecraft:wolf"
        ));

        tier2polymorphs.addAll(Arrays.asList(
                "minecraft:axolotl", "minecraft:bat", "minecraft:bee", "minecraft:cave_spider", "minecraft:cod",
                "minecraft:dolphin", "minecraft:mooshroom", "minecraft:parrot", "minecraft:pufferfish", "minecraft:salmon",
                "minecraft:polar_bear", "minecraft:squid", "minecraft:tropical_fish", "minecraft:turtle"
        ));

        tier3polymorphs.addAll(Arrays.asList(
                "minecraft:creeper", "minecraft:drowned", "minecraft:glow_squid", "minecraft:husk", "minecraft:iron_golem",
                "minecraft:piglin", "minecraft:ravager", "minecraft:silverfish", "minecraft:skeleton", "minecraft:skeleton_horse",
                "minecraft:slime", "minecraft:snow_golem", "minecraft:stray", "minecraft:strider", "minecraft:zombie",
                "minecraft:zombie_horse", "minecraft:zombified_piglin"
        ));

        tier4polymorphs.addAll(Arrays.asList(
                "minecraft:blaze", "minecraft:enderman", "minecraft:endermite", "minecraft:ghast", "minecraft:giant",
                "minecraft:guardian", "minecraft:hoglin", "minecraft:magma_cube", "minecraft:phantom", "minecraft:piglin_brute",
                "minecraft:shulker", "minecraft:vex", "minecraft:wither_skeleton", "minecraft:zoglin"
        ));

        return Arrays.asList(tier1polymorphs, tier2polymorphs, tier3polymorphs, tier4polymorphs);
    }
}