/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class UseItemTickingSoundInstance extends AbstractTickableSoundInstance {
    private final Player player;

    public UseItemTickingSoundInstance(SoundEvent event, Player player) {
        super(event, SoundSource.PLAYERS, RandomSource.create());
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.15F;
    }

    public void tick() {
        if (this.player.getUseItemRemainingTicks() <= 0) {
            this.volume -= 0.1F;
            if (this.volume <= 0.0F) {
                this.stop();
            }
        }
    }
}
