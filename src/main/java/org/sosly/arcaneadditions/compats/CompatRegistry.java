/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats;

import net.minecraftforge.fml.ModList;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.compats.BMorph.BMorphCompat;
import org.sosly.arcaneadditions.compats.Grass_Slabs.GrassSlabCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;


public class CompatRegistry {
    private static final Map<String, Supplier<Callable<ICompat>>> compatFactories = new HashMap<>();

    static {
        compatFactories.put(CompatModIDs.BMORPH, () -> BMorphCompat::new);
        compatFactories.put(CompatModIDs.GRASS_SLABS, () -> GrassSlabCompat::new);
    }

    public  static void registerCompats() {
        for (Map.Entry<String, Supplier<Callable<ICompat>>> entry : compatFactories.entrySet()) {
            if (ModList.get().isLoaded(entry.getKey())) {
                try {
                    entry.getValue().get().call().setup();
                } catch (Exception e) {
                    ArcaneAdditions.LOGGER.error("Error instantiating compatibility ", e);
                }
            }
        }
    }
}
