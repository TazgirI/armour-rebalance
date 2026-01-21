//package net.tazgirl.armour_rebalance.mixins;
//
//import net.tazgirl.armour_rebalance.Config;
//import org.objectweb.asm.tree.ClassNode;
//import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
//import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
//
//import java.util.List;
//import java.util.Set;
//
//public class ArmourRebalanceMixinPlugin implements IMixinConfigPlugin
//{
//
//    @Override
//    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
//    {
//        return !mixinClassName.equals("net.tazgirl.armour_rebalance.mixins.WardenMixin") || Config.WARDEN_PATCH.getAsBoolean();
//    }
//
//
//
//    @Override
//    public void onLoad(String mixinPackage)
//    {
//
//    }
//
//    @Override
//    public String getRefMapperConfig()
//    {
//        return null;
//    }
//
//    @Override
//    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
//    {
//
//    }
//
//    @Override
//    public List<String> getMixins()
//    {
//        return null;
//    }
//
//    @Override
//    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
//    {
//
//    }
//
//    @Override
//    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
//    {
//
//    }
//
//}
