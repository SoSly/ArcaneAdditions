package org.sosly.witchesandwizards.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpellsConfig {
    public static ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.BooleanValue ALLOW_SPELLCASTING_WHILE_POLYMORPHED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("wnw:components/polymorph");
        ALLOW_SPELLCASTING_WHILE_POLYMORPHED = builder.comment("Disabling this setting prevents players from casting spells while polymorphed")
                .translation("wnw.spells-config.allow_spellcasting_while_polymorphed")
                .define("allowSpellcasting", true);
        builder.pop();
        SpellsConfig.CONFIG = builder.build();
    }
}