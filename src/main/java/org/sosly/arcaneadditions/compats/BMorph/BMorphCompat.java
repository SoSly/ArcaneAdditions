/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats.BMorph;

import de.budschie.bmorph.morph.MorphItem;
import de.budschie.bmorph.morph.MorphManagerHandlers;
import de.budschie.bmorph.morph.MorphReasonRegistry;
import de.budschie.bmorph.morph.MorphUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.sosly.arcaneadditions.api.spells.components.IPolymorphProvider;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.compats.ICompat;
import org.sosly.arcaneadditions.networking.PacketHandler;
import org.sosly.arcaneadditions.networking.messages.clientbound.SyncPolymorphCapabilitiesToClient;

import java.util.Optional;


public class BMorphCompat implements ICompat, IPolymorphProvider {

    @Override
    public void setup() {}


    @Override
    public void polymorph(ServerPlayer target, ResourceLocation creature) {
        target.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
            polymorph.setHealth(target.getHealth());

            PacketHandler.network.send(PacketDistributor.PLAYER.with(() -> target),
                    new SyncPolymorphCapabilitiesToClient(polymorph));
        });

        CompoundTag nbt = new CompoundTag();
        Optional<MorphItem> morphItem = Optional.of(MorphManagerHandlers.FALLBACK
                .createMorph(ForgeRegistries.ENTITIES.getValue(creature), nbt, null, true));
        MorphUtil.morphToServer(morphItem, MorphReasonRegistry.MORPHED_BY_ABILITY.get(), target, true);
    }

    @Override
    public void unpolymorph(ServerPlayer target) {
        MorphUtil.morphToServer(Optional.empty(), MorphReasonRegistry.MORPHED_BY_ABILITY.get(), target, true);
        target.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
            target.setHealth(polymorph.getHealth());
            PacketHandler.network.send(PacketDistributor.PLAYER.with(() -> target),
                    new SyncPolymorphCapabilitiesToClient(polymorph));
        });
    }
}
