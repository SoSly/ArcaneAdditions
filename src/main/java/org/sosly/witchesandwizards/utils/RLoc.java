package org.sosly.witchesandwizards.utils;

import net.minecraft.resources.ResourceLocation;
import org.sosly.witchesandwizards.WitchesAndWizards;

public class RLoc {
    public RLoc() {}

    public static ResourceLocation create(String path) {
        return new ResourceLocation(WitchesAndWizards.MOD_ID, path);
    }
}
