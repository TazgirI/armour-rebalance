package net.tazgirl.armour_rebalance;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class Constants
{
    static float armourBaseDiv = 7;

    static float armourRamp = 12;
    static float armourExit = 20;
    static float armourRampDiv = 2;

    static float lowException = 4f;

    static float enchantLevelBonus = 4f;
    static float typeLevelModifier = 2f;

    public static float rampArmour(float a)
    {
        if (a <= armourRamp)
        {
            return a / armourRampDiv;
        }
        if (a >= armourExit)
        {
            return (armourRamp / armourRampDiv) + (armourExit - armourRamp) + ((a - armourExit) / (armourRampDiv * excessArmourModifier(a)));
        }

        return (armourRamp / armourRampDiv) + a - armourRamp;
    }

    static float excessArmourModifier(float a)
    {
        return Math.max(1 + (0.1f * ((a - armourExit) / (0.5f * armourExit))), 1);
    }


    public static float divNumeratorModifier(float a, float d, float t)
    {
        if (a > lowException)
        {
            return 1f + (float) (Math.sqrt(d) / (10 + t));
        }

        return 1f + (float) (Math.sqrt(d) / ((10 + lowException) - (a / 2) + t));
    }

    public static float enchantModifier(float levels)
    {
        return 1 + (levels * enchantLevelBonus) / 100;
    }

    public static float divDenominatorBase(float a, float levels)
    {
        return 1 + ((rampArmour(a) * enchantModifier(levels)) / armourBaseDiv);
    }

    public static defenceType findDefenceType(DamageSource damageSource)
    {
        Holder<DamageType> holder = damageSource.typeHolder();

        if(holder.is(DamageTypeTags.IS_EXPLOSION))
        {
            return defenceType.EXPLOSION;
        }

        if(holder.is(DamageTypeTags.IS_FIRE))
        {
            return defenceType.FIRE;
        }

        if(holder.is(DamageTypeTags.IS_PROJECTILE))
        {
            return defenceType.PROJECTILE;
        }

        if(holder.is(DamageTypeTags.IS_FALL))
        {
            return defenceType.FALL;
        }

        if(holder.is(Tags.DamageTypes.IS_MAGIC))
        {
            return defenceType.MAGIC;
        }

        return defenceType.DEFAULT;
    }

    public enum defenceType
    {
        DEFAULT(Map.of(Enchantments.PROTECTION, 1f)),
        FIRE(Map.of(Enchantments.PROTECTION, 0.5f,Enchantments.FIRE_PROTECTION, typeLevelModifier)),
        FALL(Map.of(Enchantments.PROTECTION, 0.75f)),
        EXPLOSION(Map.of(Enchantments.PROTECTION, 0.5f, Enchantments.BLAST_PROTECTION, typeLevelModifier)),
        PROJECTILE(Map.of(Enchantments.PROTECTION, 0.5f, Enchantments.PROJECTILE_PROTECTION, typeLevelModifier)),
        MAGIC(Map.of(Enchantments.PROTECTION, 0.8f));

        final Map<ResourceKey<Enchantment>, Float> enchantments;

        defenceType(Map<ResourceKey<Enchantment>, Float> enchantments)
        {
            this.enchantments = enchantments;
        }

        public float getLevels(LivingEntity entity)
        {
            return EnchantGetter.getProtectionLevels(entity, enchantments);
        }
    }
}
