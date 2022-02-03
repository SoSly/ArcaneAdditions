package org.sosly.arcaneadditions.utils;

import net.minecraft.resources.ResourceLocation;
import org.sosly.arcaneadditions.ArcaneAdditions;

public class RLoc {

    public static ResourceLocation create(String path) {
        return new ResourceLocation(ArcaneAdditions.MOD_ID, path);
    }
}
