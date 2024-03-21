package org.sosly.arcaneadditions.capabilities.familiar;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.api.capabilities.resource.ICastingResource;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import com.mna.capabilities.playerdata.magic.resources.CastingResourceRegistry;
import com.mna.capabilities.playerdata.magic.resources.Mana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.ArcaneAdditions;

import java.util.UUID;

public class FamiliarCapability implements IFamiliarCapability {
    private static final float FAMILIAR_HEALING_RATE = 25.0f;


    private boolean bapped = false;
    private Player caster;
    private ICastingResource castingResource = new Mana();
    private Mob familiar;
    private UUID familiarUUID;
    private long lastInteract;
    private long lastResourceTick;
    private long lastMaintenanceTick;
    private long lastHealingTick;
    private String name = "";
    private boolean orderedToStay = false;
    private EntityType<? extends Mob> type;

    @Override
    public boolean isBapped() {
        return bapped;
    }

    @Override
    public void setBapped(boolean value) {
        bapped = value;
    }

    @Override
    public Player getCaster() {
        return caster;
    }

    @Override
    public void setCaster(Player value) {
        caster = value;
    }

    @Override
    public ICastingResource getCastingResource() {
        return castingResource;
    }

    @Override
    public void setCastingResourceType(ResourceLocation resourceLocation) {
        if (resourceLocation != null && resourceLocation.getPath().isEmpty() && !this.getCastingResource().getRegistryName().equals(resourceLocation)) {
            Class<? extends ICastingResource> resource = CastingResourceRegistry.Instance.getRegisteredClass(resourceLocation);
            float amount = (this.castingResource.getAmount() != 0 ? this.castingResource.getAmount() : 0);

            try {
                this.castingResource = resource.getConstructor().newInstance();
                this.castingResource.setMaxAmountByLevel(this.getMagicLevel());
                this.castingResource.setAmount(amount);
            } catch (Exception err) {
                ArcaneAdditions.LOGGER.error("Failed to set casting resource type from identifier " + resourceLocation);
                ArcaneAdditions.LOGGER.error(err);
            }

        }
    }

    @Override
    public Mob getFamiliar() {
        if (familiar != null) {
            return familiar;
        }

        if (familiarUUID == null) {
            return null;
        }

        MinecraftServer server = getCaster().getServer();
        for (ServerLevel level : server.getAllLevels()) {
            Mob familiar = (Mob) level.getEntity(familiarUUID);
            if (familiar != null) {
                return familiar;
            }
        }

        return null;
    }

    @Override
    public void setFamiliar(Mob value) {
        familiar = value;
        familiarUUID = (value != null) ? value.getUUID() : null;
    }

    @Override
    public void setFamiliarUUID(UUID value) {
        familiarUUID = value;
    }

    @Override
    public long getLastInteract() {
        return lastInteract;
    }

    @Override
    public void setLastInteract(long value) {
        lastInteract = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        name = value;
    }

    @Override
    public boolean isOrderedToStay() {
        return orderedToStay;
    }

    @Override
    public void setOrderedToStay(boolean value) {
        orderedToStay = value;

        if (familiar instanceof TamableAnimal animal) {
            if (animal instanceof Parrot parrot && parrot.canSitOnShoulder()) {
                parrot.setEntityOnShoulder((ServerPlayer) caster);
            }
            animal.setOrderedToSit(value);
        } else if (familiar instanceof Fox fox) {
            fox.setSitting(value);
        } else if (familiar instanceof Bat bat) {
            bat.setResting(value);
        }

        if (caster != null) {
            lastInteract = caster.level().getGameTime();
        }
    }

    @Override
    public void reset() {
        this.bapped = false;
        this.castingResource = new Mana();
        this.castingResource.setAmount(0);
        this.familiar = null;
        this.familiarUUID = null;
        this.orderedToStay = false;
    }

    @Override
    public void tick() {
        if (caster == null || familiar == null) {
            return;
        }

        lastResourceTick = lastResourceTick > 0 ? lastResourceTick : caster.level().getGameTime();
        lastHealingTick = lastHealingTick > 0 ? lastHealingTick : caster.level().getGameTime();
        lastMaintenanceTick = lastMaintenanceTick > 0 ? lastMaintenanceTick : caster.level().getGameTime();

        if (lastMaintenanceTick < (caster.level().getGameTime() - 40L)) {
            // check whether the familiar's max mana needs to be updated based on the caster's magic level
            castingResource.setMaxAmountByLevel(this.getMagicLevel());
            lastMaintenanceTick = caster.level().getGameTime();

            // update the name of the familiar
            if (!familiar.getCustomName().getString().equals(name)) {
                name = familiar.getCustomName().getString();
            }

            // apply resistance effect to familiar
            IPlayerMagic magic = caster.getCapability(PlayerMagicProvider.MAGIC).orElse(null);
            MobEffectInstance resistance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, (magic.getMagicLevel()/15), true, false);
            familiar.addEffect(resistance, familiar);
        }

        // regenerate the familiar's mana
        if (getCastingResource().getAmount() < getCastingResource().getMaxAmount() && lastResourceTick < caster.level().getGameTime()) {
            int ticks = (int) (caster.level().getGameTime() - lastResourceTick);
            float rate = castingResource.getRegenerationRate(familiar);
            float regenPercentage = ticks / rate;
            float restored = regenPercentage * castingResource.getMaxAmount();
            castingResource.restore(restored);
            lastResourceTick = caster.level().getGameTime();
        }

        // regenerate the familiar's health at the cost of mana
        if (familiar.getHealth() < familiar.getMaxHealth() && lastHealingTick < (caster.level().getGameTime() - 20L)) {
            int toRestore = (int) (caster.level().getGameTime() - lastHealingTick) / 20;
            while (toRestore > 0) {
                if (familiar.getHealth() >= familiar.getMaxHealth() || castingResource.getAmount() < FAMILIAR_HEALING_RATE) {
                    // no healing needed or not enough mana
                    break;
                }
                familiar.heal(1);
                castingResource.consume(familiar, FAMILIAR_HEALING_RATE);
                toRestore--;
            }
            lastHealingTick = caster.level().getGameTime();
        } else if (lastHealingTick < (caster.level().getGameTime() - 20L)) {
            lastHealingTick = caster.level().getGameTime();
        }
    }

    @Override
    public EntityType<? extends Mob> getType() {
        return type;
    }

    @Override
    public void setType(EntityType<? extends Mob> value) {
        type = value;
    }

    private int getMagicLevel() {
        IPlayerMagic magic = caster.getCapability(PlayerMagicProvider.MAGIC).orElse(null);
        return magic.getMagicLevel() / 5; // todo: configure?
    }
}
