package org.sosly.witchesandwizards.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ClientConfig {
    public static ForgeConfigSpec CONFIG;

    public static class Artifacts {
        public static ForgeConfigSpec.BooleanValue CUSTOMIZABLE_FLOWER_CROWN;
        public static ForgeConfigSpec.ConfigValue<List<String>> FLOWER_TAGS;
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        ArrayList<String> defaultFlowerTags = new ArrayList<>();
        defaultFlowerTags.add("minecraft:small_flowers");
        defaultFlowerTags.add("minecraft:tall_flowers");

        // Artifacts
        builder.push("Artifacts");
        Artifacts.CUSTOMIZABLE_FLOWER_CROWN = builder
                .comment("Setting this to false turns flower crowns into pure decorative items")
                .translation("wnw.config.customizable_flower_crowns")
                .define("customizable_flower_crowns", true);
        Artifacts.FLOWER_TAGS = builder
                .comment("This array contains a list of tags which are used to identify flowers")
                .translation("wnw.config.flower_tags")
                .define("flower_tags", defaultFlowerTags);
        builder.pop();

        ClientConfig.CONFIG = builder.build();
    }
}