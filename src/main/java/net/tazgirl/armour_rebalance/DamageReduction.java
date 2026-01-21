package net.tazgirl.armour_rebalance;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

import java.util.Map;

public class  DamageReduction
{
    public static double applyFullReduction(DamageContainer damageContainer, LivingEntity entity, DamageSource damageSource, double damage, double armour, double toughness, boolean adjustForArmour, boolean adjustForEnchantments, boolean adjustForEffects, boolean applyChanges)
    {
        double fullReducedDamage = fullReducedDamage(entity, damageSource, damage, armour, toughness, true, true);


        double protectionLevels = !damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS) ? findDefenceType(damageSource).getLevels(entity) : 0;
        double resistanceLevels = !damageSource.is(DamageTypeTags.BYPASSES_RESISTANCE) && !damageSource.is(DamageTypeTags.BYPASSES_EFFECTS) ? getResistanceLevels(entity) : 0;
        double evalArmour = !damageSource.is(DamageTypeTags.BYPASSES_ARMOR) ? armour: !damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS) ? protectionLevels / 3: 0;

        if((armour == 0 && protectionLevels == 0 && resistanceLevels == 0) || fullReducedDamage == damage)
        {
            return damage;
        }


        BucketPercentage bucketContributions = bucketContributions(damage, evalArmour, toughness, protectionLevels, resistanceLevels);

        double fullReduction = damage - fullReducedDamage;

        double totalAppliedReduction = 0;
        float tempReduction;

        if(bucketContributions.armour() > 0 && adjustForArmour)
        {
            tempReduction = (float) (fullReduction * bucketContributions.armour());
            totalAppliedReduction += tempReduction;
            if(applyChanges)
            {
                damageContainer.setReduction(DamageContainer.Reduction.ARMOR, tempReduction);
            }
        }
        if(bucketContributions.protection() > 0 && adjustForEnchantments)
        {
            tempReduction = (float) (fullReduction * bucketContributions.protection());
            totalAppliedReduction += tempReduction;
            if(applyChanges)
            {
                damageContainer.setReduction(DamageContainer.Reduction.ENCHANTMENTS, tempReduction);
            }
        }
        if(bucketContributions.resistance() > 0 && adjustForEffects)
        {
            tempReduction = (float) (fullReduction * bucketContributions.resistance());
            totalAppliedReduction += tempReduction;
            if(applyChanges)
            {
                damageContainer.setReduction(DamageContainer.Reduction.MOB_EFFECTS, tempReduction);
            }
        }

        // BEFOREBUILD: COMMENT
//        if(entity instanceof Player || damageSource.getEntity() instanceof Player)
//        {
//            System.out.println("Incoming damage: " + damage + "  " + findDefenceType(damageSource) + "  " + armour + "  " + toughness + "\n" +
//                    "Out: " + fullReducedDamage);
//            System.out.println("Full reduction: " + fullReduction + "All buckets added together: " + ((fullReduction * bucketContributions.armour()) + (fullReduction * bucketContributions.protection()) + (fullReduction * bucketContributions.resistance())));
//
//            System.out.println("Prot levels: " + findDefenceType(damageSource).getLevels(entity));
//        }

        return (float)(damage - totalAppliedReduction);
    }

    public static double fullReducedDamage(LivingEntity entity, DamageSource damageSource, double damage, double armour, double toughness, boolean checkArmour, boolean checkMagic)
    {

        DefenceType type = findDefenceType(damageSource);

        double protectionLevels = 0f;
        double resistanceLevels = 0f;

        if(checkMagic)
        {
            if(!damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS))
            {
                protectionLevels = type.getLevels(entity);

//                if(damageSource.is(DamageTypeTags.BYPASSES_ARMOR))
//                {
//                    // Lets enchantments at least do something against damage meant to be at least reduced by prot
//                    armour = protectionLevels / 3;
//                }
            }

            if(!damageSource.is(DamageTypeTags.BYPASSES_EFFECTS))
            {
                if(entity.hasEffect(MobEffects.DAMAGE_RESISTANCE) && !damageSource.is(DamageTypeTags.BYPASSES_RESISTANCE))
                {
                    resistanceLevels = getResistanceLevels(entity);
                }
            }
        }

        if(!checkArmour || damageSource.is(DamageTypeTags.BYPASSES_ARMOR))
        {
            if(damageSource.is(DamageTypeTags.IS_FALL))
            {
                armour = armour * (protectionLevels * Constants.ffretain / 100);
            }
            else
            {
                armour = 0;
            }
        }

        return fullReducedDamage(damage, armour, toughness, protectionLevels, resistanceLevels);
    }

    public static double fullReducedDamage(double d, double a, double t, double pl, double rl)
    {

        a = resistanceArmourCalc(a, rl);
        t = resistanceToughnessCalc(t, rl);

        double divDenominator = 1 +
        (a * emod(pl))
                /
        (Constants.adiv * (1 + (damageScaleNumerator(d, t)
                /
                (Constants.arootmod * Math.max(Math.sqrt(a), 0.0001))
        )));

        return Math.min((d/divDenominator),d);
    }

    public static double damageScaleNumerator(double d, double t)
    {
        return Math.max((Constants.dmod * d) * (1 - ((Constants.tmod * t) / d)), 0);
    }

    public static double emod(double pl)
    {
        return 1 + ((pl * Constants.pa) / 100);
    }

    private static double resistanceArmourCalc(double armour, double resistanceLevels)
    {
        for(int i = 0; i < resistanceLevels; i++)
        {
            double raw = armour + Constants.raraw;
            double percent = armour * resistanceArmourModifier();

            if(percent > raw)
            {
                armour *= resistanceArmourModifier();
            }
            else
            {
                armour += Constants.raraw;
            }
        }

        return armour;
    }

    private static double resistanceToughnessCalc(double toughness, double resistanceLevels)
    {
        for(int i = 0; i < resistanceLevels; i++)
        {
            double raw = toughness + Constants.rtraw;
            double percent = toughness * resistanceToughnessModifier();

            if(percent > raw)
            {
                toughness *= resistanceToughnessModifier();
            }
            else
            {
                toughness += Constants.rtraw;
            }
        }

        return toughness;
    }

    public static double resistanceArmourModifier()
    {
        return 1 + (Constants.raperc / 100);
    }

    public static double resistanceToughnessModifier()
    {
        return 1 + (Constants.rtperc / 100);
    }

    private static float getResistanceLevels(LivingEntity entity)
    {
        MobEffectInstance instance = entity.getEffect(MobEffects.DAMAGE_RESISTANCE);
        if(instance != null)
        {
            return instance.getAmplifier() + 1;
        }

        return 0;
    }

    private static BucketPercentage bucketContributions(double damage, double armour, double toughness, double protectionLevels, double resistanceLevels)
    {
        // Recalculates fullReducedDamage here just in case it is passed slightly different values to the fullReducedDamage in the void, this way the percentages are guaranteed to be actual percentages
        double totalReduction = damage - fullReducedDamage(damage, armour, toughness, protectionLevels, resistanceLevels);

        double armourSum = 0;
        double protectionSum = 0;
        double resistanceSum = 0;

        double temp1;
        double temp2;
        double temp3 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, true, true, true);

        // A P R
        {
            temp1 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, true, false, false);
            temp2 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, true, true, false);

            armourSum += damage - temp1;
            protectionSum += temp1 - temp2;
            resistanceSum += temp2 - temp3;
        }
        // A R P
        {
            temp1 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, true, false, false);
            temp2 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, true, false, true);

            armourSum += damage - temp1;
            resistanceSum += temp1 - temp2;
            protectionSum += temp2 - temp3;
        }
        // R A P
        {
            temp1 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, false, false, true);
            temp2 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, true, false, true);

            resistanceSum += damage - temp1;
            armourSum += temp1 - temp2;
            protectionSum += temp2 - temp3;
        }
        // R P A
        {
            temp1 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, false, false, true);
            temp2 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, false, true, true);

            resistanceSum += damage - temp1;
            protectionSum += temp1 - temp2;
            armourSum += temp2 - temp3;
        }
        // P A R
        {
            temp1 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, false, true, false);
            temp2 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, true, true, false);

            protectionSum += damage - temp1;
            armourSum += temp1 - temp2;
            resistanceSum += temp2 - temp3;
        }
        // P R A
        {
            temp1 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, false, true, false);
            temp2 = shapleyEval(damage, armour, toughness, protectionLevels, resistanceLevels, false, true, true);

            protectionSum += damage - temp1;
            resistanceSum += temp1 - temp2;
            armourSum += temp2 - temp3;
        }

        double scaledArmour = armourSum / 6;
        double scaledProtection = protectionSum / 6;
        double scaledResistance = resistanceSum / 6;

        return new BucketPercentage(scaledArmour / totalReduction, scaledProtection / totalReduction, scaledResistance / totalReduction);
    }

    private static double shapleyEval(double damage, double armour, double toughness, double protectionLevels, double resistanceLevels, boolean useArmour, boolean useProtection, boolean useResistance)
    {
        return fullReducedDamage(damage, useArmour ? armour : 0,toughness, useProtection ? protectionLevels : 0,useResistance ? resistanceLevels : 0);
    }



    public static DefenceType findDefenceType(DamageSource damageSource)
    {
        Holder<DamageType> holder = damageSource.typeHolder();

        if(holder.is(DamageTypeTags.IS_EXPLOSION))
        {
            return DefenceType.EXPLOSION;
        }

        if(holder.is(DamageTypeTags.IS_FIRE))
        {
            return DefenceType.FIRE;
        }

        if(holder.is(DamageTypeTags.IS_PROJECTILE))
        {
            return DefenceType.PROJECTILE;
        }

        if(holder.is(DamageTypeTags.IS_FALL))
        {
            return DefenceType.FALL;
        }

        if(holder.is(Tags.DamageTypes.IS_MAGIC))
        {
            return DefenceType.MAGIC;
        }

        return DefenceType.DEFAULT;
    }

    public enum DefenceType
    {
        DEFAULT(Map.of(Enchantments.PROTECTION, 1.0)),
        FIRE(Map.of(Enchantments.PROTECTION, 0.5,Enchantments.FIRE_PROTECTION, Constants.pla)),
        FALL(Map.of(Enchantments.FEATHER_FALLING, 1.0)),
        EXPLOSION(Map.of(Enchantments.PROTECTION, 0.7, Enchantments.BLAST_PROTECTION, Constants.pla)),
        PROJECTILE(Map.of(Enchantments.PROTECTION, 0.5, Enchantments.PROJECTILE_PROTECTION, Constants.pla)),
        MAGIC(Map.of(Enchantments.PROTECTION, 0.8));

        final Map<ResourceKey<Enchantment>, Double> enchantments;

        DefenceType(Map<ResourceKey<Enchantment>, Double> enchantments)
        {
            this.enchantments = enchantments;
        }

        public double getLevels(LivingEntity entity)
        {
            return EnchantGetter.getProtectionLevels(entity, enchantments);
        }
    }
}
