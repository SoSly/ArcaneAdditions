/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.networking.messages.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.networking.BaseMessage;
import org.sosly.arcaneadditions.networking.messages.ServerMessageHandler;
import org.sosly.arcaneadditions.spells.FamiliarSpell;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.function.Supplier;

public class RemoveFamiliarSpell extends BaseMessage {
    private final String spellName;

    public RemoveFamiliarSpell(String spellName) {
        this.spellName = spellName;
    }

    public static RemoveFamiliarSpell decode(FriendlyByteBuf buf) {
        RemoveFamiliarSpell msg;
        
        try {
            String spellName = buf.readUtf();
            msg = new RemoveFamiliarSpell(spellName);
        } catch (IndexOutOfBoundsException | IllegalArgumentException err) {
            return null;
        }
        
        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(RemoveFamiliarSpell msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.spellName);
    }

    public static void handleRemoveFamiliarSpell(RemoveFamiliarSpell msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        if (!ServerMessageHandler.validateBasics(msg, ctx)) {
            return;
        }

        ServerPlayer player = ctx.getSender();
        if (player == null) {
            return;
        }

        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(player);
        if (cap == null || cap.getFamiliar() == null) {
            return;
        }

        if (!cap.getCaster().equals(player)) {
            return;
        }

        FamiliarSpell toRemove = cap.getSpellsKnown().stream()
            .filter(spell -> spell.getName().getString().equals(msg.spellName))
            .findFirst()
            .orElse(null);

        if (toRemove != null) {
            cap.getSpellsKnown().remove(toRemove);
        }
    }
}