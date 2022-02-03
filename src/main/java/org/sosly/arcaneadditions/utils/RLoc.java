package org.sosly.arcaneadditions.utils;

import net.minecraft.resources.ResourceLocation;

public class RLoc {
    public RLoc() {}

    public static ResourceLocation create(String path) {
        return new ResourceLocation(org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID, path);
    }
}
