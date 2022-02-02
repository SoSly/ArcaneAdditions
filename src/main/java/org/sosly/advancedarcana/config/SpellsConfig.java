package org.sosly.advancedarcana.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpellsConfig {
    public static ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.BooleanValue ALLOW_SPELLCASTING_WHILE_POLYMORPHED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("advancedarcana:components/polymorph");
        ALLOW_SPELLCASTING_WHILE_POLYMORPHED = builder.comment("Disabling this setting prevents players from casting spells while polymorphed")
                .translation("advancedarcana.spells-config.allow_spellcasting_while_polymorphed")
                .define("allowSpellcasting", true);
        builder.pop();
        SpellsConfig.CONFIG = builder.build();
    }
}