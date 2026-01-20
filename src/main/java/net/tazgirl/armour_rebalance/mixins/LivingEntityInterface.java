package net.tazgirl.armour_rebalance.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Stack;

@Mixin(LivingEntity.class)
public interface LivingEntityInterface
{
    @Accessor("damageContainers")
    Stack<DamageContainer> getDamageContainers();
}
