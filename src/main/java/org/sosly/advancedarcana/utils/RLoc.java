package org.sosly.advancedarcana.utils;

import net.minecraft.resources.ResourceLocation;
import org.sosly.advancedarcana.AdvancedArcana;

public class RLoc {
    public RLoc() {}

    public static ResourceLocation create(String path) {
        return new ResourceLocation(AdvancedArcana.MOD_ID, path);
    }
}
