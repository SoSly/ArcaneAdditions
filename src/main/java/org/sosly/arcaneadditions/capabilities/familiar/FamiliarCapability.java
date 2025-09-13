package org.sosly.arcaneadditions.capabilities.familiar;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.api.capabilities.IPlayerProgression;
import com.mna.api.capabilities.resource.ICastingResource;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import com.mna.capabilities.playerdata.magic.resources.CastingResourceRegistry;
import com.mna.capabilities.playerdata.magic.resources.Mana;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.config.ServerConfig;
import org.sosly.arcaneadditions.entities.ai.Constants;
import org.sosly.arcaneadditions.spells.FamiliarSpell;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FamiliarCapability implements IFamiliarCapability {
    private boolean bapped = false;
    private Player caster;
    private ICastingResource castingResource = new Mana();
    private Mob familiar;
    private UUID familiarUUID;
    private CompoundTag familiarNBT;
    private long lastInteract;
    private long lastResourceTick;
    private long lastMaintenanceTick;
    private long lastHealingTick;
    private Level loadLevel;
    private BlockPos loadPos;
    private String name = "";
    private boolean orderedToStay = false;
    private LinkedHashSet<FamiliarSpell> spellsKnown = new LinkedHashSet<>();
    private EntityType<? extends Mob> type;
    private ResourceKey<Level> lastKnownDimension;

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
        if (resourceLocation == null) {
            return;
        }

        if (resourceLocation.getPath().isEmpty()) {
            return;
        }

        if (this.getCastingResource().getRegistryName().equals(resourceLocation)) {
            return;
        }

        Class<? extends ICastingResource> resource = CastingResourceRegistry.Instance.getRegisteredClass(resourceLocation);
        float amount = this.castingResource.getAmount();

        try {
            this.castingResource = resource.getConstructor().newInstance();
            this.castingResource.setMaxAmountByLevel(this.getMagicLevel());
            this.castingResource.setAmount(amount);
        } catch (Exception err) {
            ArcaneAdditions.LOGGER.error("Failed to set casting resource type from identifier " + resourceLocation);
            ArcaneAdditions.LOGGER.error(err);
        }
    }

    @Override
    public Mob getFamiliar() {
        if (familiar != null && !familiar.isRemoved() && familiar.level().dimension().equals(caster.level().dimension())) {
            return familiar;
        }

        if (familiarUUID == null) {
            return null;
        }

        MinecraftServer server = getCaster().getServer();
        if (server == null) {
            return null;
        }

        if (lastKnownDimension != null) {
            Mob found = searchDimension(server.getLevel(lastKnownDimension));
            if (found != null) {
                familiar = found;
                return familiar;
            }
        }

        for (ServerLevel level : server.getAllLevels()) {
            if (lastKnownDimension != null && level.dimension().equals(lastKnownDimension)) {
                continue;
            }

            Mob found = searchDimension(level);
            if (found == null) {
                continue;
            }

            familiar = found;
            lastKnownDimension = level.dimension();
            return familiar;
        }

        familiar = null;
        lastKnownDimension = null;
        return null;
    }

    private Mob searchDimension(ServerLevel level) {
        if (level == null) {
            return null;
        }

        Mob entity = (Mob) level.getEntity(familiarUUID);
        if (entity == null || entity.isRemoved()) {
            return null;
        }

        return entity;
    }

    @Override
    public void setFamiliar(Mob value) {
        familiar = value;
        familiarUUID = (value != null) ? value.getUUID() : null;
        lastKnownDimension = (value != null) ? value.level().dimension() : null;
    }

    @Override
    public void setFamiliarUUID(UUID value) {
        familiarUUID = value;
        familiar = null;
        lastKnownDimension = null;
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
    public void loadOnNextTick(Level level, BlockPos pos) {
        loadLevel = level;
        loadPos = pos;
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

        if (familiar == null) {
            return;
        }

        String[] methodNames = {"setInSittingPose", "setOrderedToSit", "setSitting", "setResting", "setInSitPose", "setEntityOnShoulder"};
        Class<?> clazz = familiar.getClass();

        for (String methodName : methodNames) {
            try {
                java.lang.reflect.Method method = clazz.getMethod(methodName, boolean.class);
                method.invoke(familiar, value);
                break;
            } catch (NoSuchMethodException ignored) {
                // This method doesn't exist, try the next one
            } catch (Exception e) {
                ArcaneAdditions.LOGGER.debug("Failed to invoke " + methodName + " on " + familiar.getType().getDescriptionId());
            }
        }

        if (caster != null) {
            lastInteract = caster.level().getGameTime();
        }
    }

    @Override
    public CompoundTag getFamiliarNBT() {
        return familiarNBT;
    }

    @Override
    public void setFamiliarNBT(CompoundTag nbt) {
        familiarNBT = nbt;
    }

    @Override
    public void storeFamiliarData() {
        if (familiar == null || familiar.isRemoved()) {
            return;
        }

        CompoundTag nbt = new CompoundTag();
        familiar.saveWithoutId(nbt);

        nbt.remove("Pos");
        nbt.remove("Motion");
        nbt.remove("Rotation");
        nbt.remove("FallDistance");
        nbt.remove("Fire");
        nbt.remove("Air");
        nbt.remove("OnGround");
        nbt.remove("Dimension");
        nbt.remove("PortalCooldown");
        nbt.remove("UUID");

        familiarNBT = nbt;
    }

    @Override
    public void reset() {
        this.bapped = false;
        this.castingResource = new Mana();
        this.castingResource.setAmount(0);
        this.familiar = null;
        this.familiarUUID = null;
        this.familiarNBT = null;
        this.lastKnownDimension = null;
        this.orderedToStay = false;
        this.spellsKnown = new LinkedHashSet<>();
    }

    @Override
    public void addSpellKnown(FamiliarSpell spell, boolean checkTiers) {
        AtomicInteger i = new AtomicInteger();
        final int maxKnown;
        if (checkTiers) {
            if (caster == null) {
                return;
            }
            IPlayerProgression progression = caster.getCapability(PlayerProgressionProvider.PROGRESSION).orElse(null);
            maxKnown = progression.getTier() - 2;
        } else {
            maxKnown = 3;
        }
        spellsKnown.removeIf((s) -> (i.getAndIncrement() > maxKnown || s.getName().equals(spell.getName())));
        spellsKnown.add(spell);
    }

    @Override
    public Collection<FamiliarSpell> getSpellsKnown() {
        return spellsKnown;
    }

    @Override
    public void tick() {
        if (loadLevel != null && loadPos != null) {
            if (FamiliarHelper.createFamiliar(caster, type, Component.literal(name), loadLevel, loadPos)) {
                loadLevel = null;
                loadPos = null;
            }
            return;
        }

        if (caster == null || familiar == null) {
            return;
        }

        lastResourceTick = lastResourceTick > 0 ? lastResourceTick : caster.level().getGameTime();
        lastHealingTick = lastHealingTick > 0 ? lastHealingTick : caster.level().getGameTime();
        lastMaintenanceTick = lastMaintenanceTick > 0 ? lastMaintenanceTick : caster.level().getGameTime();

        if (lastMaintenanceTick < (caster.level().getGameTime() - Constants.MAINTENANCE_TICK_INTERVAL)) {
            castingResource.setMaxAmountByLevel(this.getMagicLevel());
            lastMaintenanceTick = caster.level().getGameTime();

            if (!familiar.getCustomName().getString().equals(name)) {
                name = familiar.getCustomName().getString();
            }

            IPlayerMagic magic = caster.getCapability(PlayerMagicProvider.MAGIC).orElse(null);
            MobEffectInstance resistance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, (magic.getMagicLevel()/15), true, false);
            familiar.addEffect(resistance, familiar);
        }

        if (getCastingResource().getAmount() < getCastingResource().getMaxAmount() && lastResourceTick < caster.level().getGameTime()) {
            int ticks = (int) (caster.level().getGameTime() - lastResourceTick);
            float rate = castingResource.getRegenerationRate(familiar);
            float regenPercentage = ticks / rate;
            float restored = regenPercentage * castingResource.getMaxAmount();
            castingResource.restore(restored);
            lastResourceTick = caster.level().getGameTime();
        }

        if (familiar.getHealth() < familiar.getMaxHealth() && lastHealingTick < (caster.level().getGameTime() - ServerConfig.familiarHealingTickInterval)) {
            int toRestore = (int) (caster.level().getGameTime() - lastHealingTick) / ServerConfig.familiarHealingTickInterval;
            while (toRestore > 0) {
                if (familiar.getHealth() >= familiar.getMaxHealth() || castingResource.getAmount() < ServerConfig.familiarHealingRate) {
                    break;
                }
                familiar.heal(1);
                castingResource.consume(familiar, (float)ServerConfig.familiarHealingRate);
                toRestore--;
            }
            lastHealingTick = caster.level().getGameTime();
        } else if (lastHealingTick < (caster.level().getGameTime() - ServerConfig.familiarHealingTickInterval)) {
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
        return magic.getMagicLevel() / Constants.MAGIC_LEVEL_DIVISOR;
    }

    public boolean validateFamiliar() {
        if (familiar == null && familiarUUID == null) {
            return true;
        }

        if (familiar != null && familiar.isRemoved()) {
            familiar = null;
            familiarUUID = null;
            lastKnownDimension = null;
            return false;
        }

        if (familiarUUID != null && familiar == null && caster.getServer() != null) {
            for (ServerLevel level : caster.getServer().getAllLevels()) {
                Mob found = (Mob) level.getEntity(familiarUUID);
                if (found != null && !found.isRemoved()) {
                    return true;
                }
            }
            familiarUUID = null;
            lastKnownDimension = null;
            return false;
        }

        return true;
    }
}
