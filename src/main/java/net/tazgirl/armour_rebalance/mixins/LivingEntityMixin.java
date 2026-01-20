package net.tazgirl.armour_rebalance.mixins;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.tazgirl.armour_rebalance.DamageReduction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Stack;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Shadow
    @Nullable
    protected Stack<DamageContainer> damageContainers;

    @Shadow
    protected abstract void hurtArmor(DamageSource damageSource, float damageAmount);

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    public void getDamageAfterMagicAbsorb(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir)
    {
        LivingEntity me = (LivingEntity) (Object) this;

        float damageAfterMagicReduction = (float) DamageReduction.applyFullReduction(damageContainers.peek(), me, damageSource, damageAmount, me.getAttributeValue(Attributes.ARMOR), me.getAttributeValue(Attributes.ARMOR_TOUGHNESS), false, true, true, true);

        cir.setReturnValue(damageAfterMagicReduction);
        cir.cancel();
    }


    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    public void getDamageAfterArmorAbsorb(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir)
    {
        LivingEntity me = (LivingEntity)(Object) this;

        if(!damageSource.is(DamageTypeTags.BYPASSES_ARMOR))
        {
            hurtArmor(damageSource, damageAmount);
        }
        else
        {
            cir.setReturnValue(damageAmount);
            cir.cancel();
            return;
        }
        float damageAfterArmourReduction = (float) DamageReduction.applyFullReduction(damageContainers.peek(), me, damageSource, damageAmount, me.getAttributeValue(Attributes.ARMOR), me.getAttributeValue(Attributes.ARMOR_TOUGHNESS), true, false, false, false);

        cir.setReturnValue(damageAfterArmourReduction);
        cir.cancel();
    }

    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/damagesource/DamageContainer;setReduction(Lnet/neoforged/neoforge/common/damagesource/DamageContainer$Reduction;F)V"))
    private static void skipArmourReduction(DamageContainer instance, DamageContainer.Reduction reduction, float amount)
    {
        if(reduction == DamageContainer.Reduction.ARMOR)
        {
            return;
        }

        instance.setReduction(reduction, amount);
    }

    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private static float skipMagicAbsorb(LivingEntity instance, DamageSource j, float f)
    {
        return f;
    }

    @Inject(method = "actuallyHurt",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", shift = At.Shift.AFTER))
    public void runFullDamageCalc(DamageSource damageSource, float damageAmount, CallbackInfo ci)
    {
        DamageContainer damageContainer = damageContainers.peek();
        LivingEntity me = (LivingEntity)(Object) this;

        DamageReduction.applyFullReduction(damageContainer, me, damageSource, damageAmount, (float) me.getAttributeValue(Attributes.ARMOR),(float) me.getAttributeValue(Attributes.ARMOR_TOUGHNESS), true, true, true, true);
    }
}

