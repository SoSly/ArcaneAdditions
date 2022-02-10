/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.utils;

import net.minecraft.resources.ResourceLocation;
import org.sosly.arcaneadditions.ArcaneAdditions;

public class RLoc {

    public static ResourceLocation create(String path) {
        return new ResourceLocation(ArcaneAdditions.MOD_ID, path);
    }
}
