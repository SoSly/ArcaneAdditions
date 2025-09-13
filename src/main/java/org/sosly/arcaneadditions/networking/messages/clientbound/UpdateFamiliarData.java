/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.networking.messages.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.sosly.arcaneadditions.gui.screens.FamiliarScreen;
import org.sosly.arcaneadditions.networking.BaseMessage;

import java.util.function.Supplier;

public class UpdateFamiliarData extends BaseMessage {
    private final float health;
    private final float maxHealth;
    private final float mana;
    private final float maxMana;

    public UpdateFamiliarData(float health, float maxHealth, float mana, float maxMana) {
        this.health = health;
        this.maxHealth = maxHealth;
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public static UpdateFamiliarData decode(FriendlyByteBuf buf) {
        float health = buf.readFloat();
        float maxHealth = buf.readFloat();
        float mana = buf.readFloat();
        float maxMana = buf.readFloat();
        
        UpdateFamiliarData msg = new UpdateFamiliarData(health, maxHealth, mana, maxMana);
        msg.messageIsValid = true;
        return msg;
    }

    public static void encode(UpdateFamiliarData msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.health);
        buf.writeFloat(msg.maxHealth);
        buf.writeFloat(msg.mana);
        buf.writeFloat(msg.maxMana);
    }

    public static void handleUpdateFamiliarData(UpdateFamiliarData msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof FamiliarScreen familiarScreen) {
                familiarScreen.updateFamiliarData(msg.health, msg.maxHealth, msg.mana, msg.maxMana);
            }
        });
        ctx.setPacketHandled(true);
    }
}