package net.tazgirl.armour_rebalance.mixins;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.tazgirl.armour_rebalance.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public class WardenMixin
{
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void finalizeSpawn(CallbackInfoReturnable<AttributeSupplier.Builder> cir)
    {
        Warden warden = (Warden) (Object)this;

        AttributeInstance attribute = warden.getAttribute(Attributes.ATTACK_DAMAGE);

        if(attribute.getBaseValue() == 30.0 && Constants.wardenPatch)
        {
            attribute.setBaseValue(23.3333);
        }
    }

}
