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

    // Familiar AI Settings
    static {
        BUILDER.push("familiar_ai");
        BUILDER.comment("Familiar AI behavior settings");
    }

    // Familiar Behavior
    private static final ForgeConfigSpec.DoubleValue FAMILIAR_STAY_DISTANCE_THRESHOLD = BUILDER
            .comment("Distance in blocks at which familiar ignores stay order if caster is hurt")
            .translation("config.arcaneadditions.familiar_stay_distance_threshold")
            .defineInRange("stayDistanceThreshold", 144.0, 1.0, 1024.0);

    private static final ForgeConfigSpec.IntValue FAMILIAR_TELEPORT_DISTANCE = BUILDER
            .comment("Random blocks offset when teleporting to caster")
            .translation("config.arcaneadditions.familiar_teleport_distance")
            .defineInRange("teleportDistance", 3, 1, 10);

    private static final ForgeConfigSpec.IntValue FAMILIAR_TELEPORT_ATTEMPTS = BUILDER
            .comment("Maximum attempts to find safe teleport location")
            .translation("config.arcaneadditions.familiar_teleport_attempts")
            .defineInRange("teleportAttempts", 10, 1, 50);

    private static final ForgeConfigSpec.IntValue FAMILIAR_PATH_RECALC_DELAY = BUILDER
            .comment("Ticks between path recalculations")
            .translation("config.arcaneadditions.familiar_path_recalc_delay")
            .defineInRange("pathRecalcDelay", 10, 1, 100);

    private static final ForgeConfigSpec.DoubleValue FAMILIAR_LOOK_AT_SPEED = BUILDER
            .comment("Rotation speed when looking at caster")
            .translation("config.arcaneadditions.familiar_look_at_speed")
            .defineInRange("lookAtSpeed", 10.0, 1.0, 45.0);

    // Spellcasting
    private static final ForgeConfigSpec.IntValue SPELL_CAST_ATTEMPT_COOLDOWN = BUILDER
            .comment("Minimum ticks between spell cast attempts")
            .translation("config.arcaneadditions.spell_cast_attempt_cooldown")
            .defineInRange("spellCastAttemptCooldown", 20, 1, 100);

    private static final ForgeConfigSpec.IntValue SPELL_MINIMUM_MANA_BUFFER = BUILDER
            .comment("Mana to keep in reserve after casting")
            .translation("config.arcaneadditions.spell_minimum_mana_buffer")
            .defineInRange("spellMinimumManaBuffer", 10, 0, 100);

    private static final ForgeConfigSpec.IntValue SPELL_SIGHT_REQUIRED_TIME = BUILDER
            .comment("Ticks target must be visible before casting")
            .translation("config.arcaneadditions.spell_sight_required_time")
            .defineInRange("spellSightRequiredTime", 20, 1, 100);

    private static final ForgeConfigSpec.IntValue SPELL_STRAFING_RECALC_TIME = BUILDER
            .comment("Ticks before changing strafe direction")
            .translation("config.arcaneadditions.spell_strafing_recalc_time")
            .defineInRange("spellStrafingRecalcTime", 20, 1, 100);

    private static final ForgeConfigSpec.DoubleValue SPELL_STRAFING_CHANCE = BUILDER
            .comment("Chance to change strafe direction")
            .translation("config.arcaneadditions.spell_strafing_chance")
            .defineInRange("spellStrafingChance", 0.3, 0.0, 1.0);

    private static final ForgeConfigSpec.DoubleValue SPELL_STRAFING_SPEED = BUILDER
            .comment("Movement speed multiplier while strafing")
            .translation("config.arcaneadditions.spell_strafing_speed")
            .defineInRange("spellStrafingSpeed", 0.5, 0.1, 2.0);

    private static final ForgeConfigSpec.DoubleValue SPELL_STRAFING_BACKWARD_THRESHOLD = BUILDER
            .comment("Distance ratio to stop backing up")
            .translation("config.arcaneadditions.spell_strafing_backward_threshold")
            .defineInRange("spellStrafingBackwardThreshold", 0.75, 0.1, 1.0);

    private static final ForgeConfigSpec.DoubleValue SPELL_STRAFING_FORWARD_THRESHOLD = BUILDER
            .comment("Distance ratio to start backing up")
            .translation("config.arcaneadditions.spell_strafing_forward_threshold")
            .defineInRange("spellStrafingForwardThreshold", 0.25, 0.1, 1.0);

    private static final ForgeConfigSpec.DoubleValue SPELL_LOOK_AT_SPEED = BUILDER
            .comment("Rotation speed when targeting for spells")
            .translation("config.arcaneadditions.spell_look_at_speed")
            .defineInRange("spellLookAtSpeed", 30.0, 1.0, 90.0);

    private static final ForgeConfigSpec.DoubleValue SPELL_NAVIGATION_SPEED = BUILDER
            .comment("Movement speed toward spell target")
            .translation("config.arcaneadditions.spell_navigation_speed")
            .defineInRange("spellNavigationSpeed", 1.0, 0.1, 3.0);

    // Wandering
    private static final ForgeConfigSpec.IntValue WANDER_IDLE_THRESHOLD = BUILDER
            .comment("No-action ticks before wandering")
            .translation("config.arcaneadditions.wander_idle_threshold")
            .defineInRange("wanderIdleThreshold", 100, 20, 1000);

    private static final ForgeConfigSpec.IntValue WANDER_SEARCH_RADIUS = BUILDER
            .comment("Horizontal blocks to search for wander target")
            .translation("config.arcaneadditions.wander_search_radius")
            .defineInRange("wanderSearchRadius", 10, 1, 32);

    private static final ForgeConfigSpec.IntValue WANDER_SEARCH_HEIGHT = BUILDER
            .comment("Vertical blocks to search for wander target")
            .translation("config.arcaneadditions.wander_search_height")
            .defineInRange("wanderSearchHeight", 7, 1, 16);

    private static final ForgeConfigSpec.IntValue WANDER_MAX_ATTEMPTS = BUILDER
            .comment("Maximum attempts to find valid wander position")
            .translation("config.arcaneadditions.wander_max_attempts")
            .defineInRange("wanderMaxAttempts", 10, 1, 50);

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
    
    // Familiar AI cached values
    public static double familiarStayDistanceThreshold;
    public static int familiarTeleportDistance;
    public static int familiarTeleportAttempts;
    public static int familiarPathRecalcDelay;
    public static double familiarLookAtSpeed;
    public static int spellCastAttemptCooldown;
    public static int spellMinimumManaBuffer;
    public static int spellSightRequiredTime;
    public static int spellStrafingRecalcTime;
    public static double spellStrafingChance;
    public static double spellStrafingSpeed;
    public static double spellStrafingBackwardThreshold;
    public static double spellStrafingForwardThreshold;
    public static double spellLookAtSpeed;
    public static double spellNavigationSpeed;
    public static int wanderIdleThreshold;
    public static int wanderSearchRadius;
    public static int wanderSearchHeight;
    public static int wanderMaxAttempts;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        familiars = FAMILIARS.get();
        polymorphAllowSpellcasting = POLYMORPH_ALLOW_SPELLCASTING.get();
        polymorphTiers = POLYMORPH_TIERS.get();
        soulSearchersLensCreatureModifiers = SOUL_SEARCHERS_LENS_CREATURE_MODIFIERS.get();
        soulSearchersLensHealthPerLevel = SOUL_SEARCHERS_LENS_HEALTH_PER_LEVEL.get();
        soulSearchersLensMaxDistance = SOUL_SEARCHERS_LENS_MAX_DISTANCE.get();
        
        // Load familiar AI config values
        familiarStayDistanceThreshold = FAMILIAR_STAY_DISTANCE_THRESHOLD.get();
        familiarTeleportDistance = FAMILIAR_TELEPORT_DISTANCE.get();
        familiarTeleportAttempts = FAMILIAR_TELEPORT_ATTEMPTS.get();
        familiarPathRecalcDelay = FAMILIAR_PATH_RECALC_DELAY.get();
        familiarLookAtSpeed = FAMILIAR_LOOK_AT_SPEED.get();
        spellCastAttemptCooldown = SPELL_CAST_ATTEMPT_COOLDOWN.get();
        spellMinimumManaBuffer = SPELL_MINIMUM_MANA_BUFFER.get();
        spellSightRequiredTime = SPELL_SIGHT_REQUIRED_TIME.get();
        spellStrafingRecalcTime = SPELL_STRAFING_RECALC_TIME.get();
        spellStrafingChance = SPELL_STRAFING_CHANCE.get();
        spellStrafingSpeed = SPELL_STRAFING_SPEED.get();
        spellStrafingBackwardThreshold = SPELL_STRAFING_BACKWARD_THRESHOLD.get();
        spellStrafingForwardThreshold = SPELL_STRAFING_FORWARD_THRESHOLD.get();
        spellLookAtSpeed = SPELL_LOOK_AT_SPEED.get();
        spellNavigationSpeed = SPELL_NAVIGATION_SPEED.get();
        wanderIdleThreshold = WANDER_IDLE_THRESHOLD.get();
        wanderSearchRadius = WANDER_SEARCH_RADIUS.get();
        wanderSearchHeight = WANDER_SEARCH_HEIGHT.get();
        wanderMaxAttempts = WANDER_MAX_ATTEMPTS.get();
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