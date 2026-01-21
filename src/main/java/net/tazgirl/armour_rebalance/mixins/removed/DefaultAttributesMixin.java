//package net.tazgirl.armour_rebalance.mixins;
//
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
//import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
//import org.spongepowered.asm.mixin.*;
//
//import java.util.Map;
//
//@Mixin(DefaultAttributes.class)
//public class DefaultAttributesMixin
//{
//
//    @Mutable
//    @Final
//    @Shadow
//    private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> SUPPLIERS;
//
//    @Unique
//    private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> armour_rebalance$supplierMap()
//    {
//        return SUPPLIERS;
//    }
//}
