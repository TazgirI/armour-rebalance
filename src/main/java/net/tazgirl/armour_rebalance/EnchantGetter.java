package net.tazgirl.armour_rebalance;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EnchantGetter
{
    // Attempted to copy how vanilla does it, runs mainly on crossed fingers
    public static double getProtectionLevels(LivingEntity entity, Map<ResourceKey<Enchantment>, Double> protectionEnchantments)
    {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.runIterationOnEquipment(entity, (holder, slot, ctx) -> {
            Double d = protectionEnchantments.get(holder.getKey());
            if(d != null)
            {
                mutableFloat.addAndGet(ctx.itemStack().getEnchantmentLevel(holder) * d);
            }
        });

        return mutableFloat.floatValue();
    }


}
