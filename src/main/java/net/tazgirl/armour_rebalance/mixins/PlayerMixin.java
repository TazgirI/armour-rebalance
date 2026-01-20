package net.tazgirl.armour_rebalance.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.tazgirl.armour_rebalance.DamageReduction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin
{

    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/damagesource/DamageContainer;setReduction(Lnet/neoforged/neoforge/common/damagesource/DamageContainer$Reduction;F)V"))
    private static void skipArmourReduction(DamageContainer instance, DamageContainer.Reduction reduction, float amount)
    {
        if(reduction == DamageContainer.Reduction.ARMOR)
        {
            return;
        }

        instance.setReduction(reduction, amount);
    }

    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private static float skipMagicAbsorb(Player instance, DamageSource damageSource, float v)
    {
        return v;
    }

    @Inject(method = "actuallyHurt",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", shift = At.Shift.AFTER))
    public void runFullDamageCalc(DamageSource damageSource, float damageAmount, CallbackInfo ci)
    {
        DamageContainer damageContainer = ((LivingEntityInterface)this).getDamageContainers().peek();
        LivingEntity me = (LivingEntity)(Object) this;

        DamageReduction.applyFullReduction(damageContainer, me, damageSource, damageAmount, (float) me.getAttributeValue(Attributes.ARMOR),(float) me.getAttributeValue(Attributes.ARMOR_TOUGHNESS), true, true, true, true);
    }


}
