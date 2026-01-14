package net.tazgirl.armour_rebalance.mixins;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.tazgirl.armour_rebalance.ArmourRebalance;
import net.tazgirl.armour_rebalance.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombatRules.class)
public class CombatRulesMixin
{

    /**
     * @author Tazgirl
     * @reason This entire mods purpose is to overwrite these methods
     */
    @Overwrite
    public static float getDamageAfterAbsorb(LivingEntity entity, float damage, DamageSource damageSource, float armorValue, float armorToughness)
    {
        // Even with lowException there is some dodgy calculations when armour == 1, this fixes it with negligible change to the rest of the calculations
        // 0 armour still takes full damage because the lead in where the calculations return over damage and thus get clamped to damage is just over 1 in length
        armorValue += 1;

        Constants.defenceType type = Constants.findDefenceType(damageSource);

        float protectionLevels = 0;

        if(!damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS))
        {
            protectionLevels = type.getLevels(entity);
        }

        float divNumerator = damage * Constants.divNumeratorModifier(armorValue, damage, armorToughness);
        float divDenominator = Constants.divDenominatorBase(armorValue, protectionLevels);

        if(entity instanceof Player || damageSource.getEntity() instanceof Player)
        {
            ArmourRebalance.LOGGER.warn("Ramped: " + armorValue + " to " + Constants.rampArmour(armorValue));

            ArmourRebalance.LOGGER.warn("Incoming damage: " + damage + "  " + Constants.findDefenceType(damageSource) + "  " + armorValue + "  " + armorToughness + "\n" +
                    "Out: " + Math.min((divNumerator/divDenominator),damage));
            System.out.println("Incoming damage: " + damage + "  " + Constants.findDefenceType(damageSource) + "  " + armorValue + "  " + armorToughness + "\n" +
                    "Out: " + Math.min((divNumerator/divDenominator),damage));
        }

        return Math.min((divNumerator/divDenominator),damage);
    }


}
