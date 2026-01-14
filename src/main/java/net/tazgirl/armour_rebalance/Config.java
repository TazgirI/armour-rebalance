package net.tazgirl.armour_rebalance;

import java.util.List;

import com.mojang.datafixers.kinds.Const;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.naming.ConfigurationException;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> INFO = BUILDER.comment("To see how all of these values are used and the impact of any adjustments, check the interactable graph here: https://www.desmos.com/calculator/0nycykas7b").define("info", "");

    public static final ModConfigSpec.ConfigValue<Float> ARMOUR_BASE_DIV = BUILDER
            .comment("Base armour div")
            .define("armourBaseDiv", 7f, (f -> f instanceof Float && (float) f > 0));

    public static final ModConfigSpec.ConfigValue<String> RAMPS_INFO = BUILDER.comment("""
            The following values are used to adjust the armour value to find its true value: \
            
             armour <= armourRamp, armour is divided by rampDiv \
            
             armourRamp < armour < armourExit, armour is applied in a 1:1 ratio\
            
             armour >= armourExit, armour is divided by (rampDiv * excessModifier), check the graph to see how the excessModifier is calculated\
            
             It's important to note that these values are not mutually exclusive, if armour = 15 when ramp = 10 then rampedArmour = (10/rampDiv) + 5""").define("rampsInfo", "");


    public static final ModConfigSpec.ConfigValue<Float> ARMOUR_RAMP = BUILDER
            .comment("Armour ramp period, armour <= this is part of the ramp)")
            .define("armourRamp", 12f, (f -> f instanceof Float && (float) f >= 0));

    public static final ModConfigSpec.ConfigValue<Float> ARMOUR_EXIT = BUILDER
            .comment("Exit of armour default period, armour >= this is part of the exit ramp)")
            .define("armourExit", 20f, (f -> f instanceof Float && (float) f >= 0));

    public static final ModConfigSpec.ConfigValue<Float> ARMOUR_RAMP_DIV = BUILDER
            .comment("Applied to armour that's part of the entrance or exit ramp")
            .define("armourRampDiv", 2f, (f -> f instanceof Float && (float) f >= 0));

    public static final ModConfigSpec.ConfigValue<Float> LOW_EXCEPTION = BUILDER
            .comment("If the raw armour value is <= lowException then the armour effectiveness dropoff is calculated slightly differently, is used so tiny amounts of armour don't provide 0 protection")
            .define("lowException", 4f, (f -> f instanceof Float && (float) f >= 0));

    public static final ModConfigSpec.ConfigValue<Float> ENCHANT_LEVEL_BONUS = BUILDER
            .comment("How much of a percentage bonus each level of protection gives to the ramped armour value")
            .define("enchantLevelBonus", 4f, (f -> f instanceof Float && (float) f >= 0));

    public static final ModConfigSpec.ConfigValue<Float> ENCHANT_TYPE_MODIFIER = BUILDER
            .comment("Is used to multiply the found levels of protection if they are considered the right type to match the damage type")
            .define("enchantLevelBonus", 2f, (f -> f instanceof Float && (float) f >= 1f));



    public static void SyncConfigValues(ModConfigEvent event)
    {
        Constants.armourBaseDiv = ARMOUR_BASE_DIV.get();

        Constants.armourRamp = ARMOUR_RAMP.get();
        Constants.armourExit = ARMOUR_EXIT.get();
        if(Constants.armourRamp >= Constants.armourExit && !(Constants.armourRamp == 0 && Constants.armourExit == 0))
        {
            throw new RuntimeException("armourRamp value " + Constants.armourRamp + " is >= armourExit value " + Constants.armourExit + " in the armour_rebalance config \nEither make armourRamp lower than armourExit or make both 0 to disable the ramp system");
        }
        Constants.armourRampDiv = ARMOUR_RAMP_DIV.get();

        Constants.lowException = LOW_EXCEPTION.get();

        Constants.enchantLevelBonus = ENCHANT_LEVEL_BONUS.get();
        Constants.typeLevelModifier = ENCHANT_TYPE_MODIFIER.get();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void OnConfigLoading(ModConfigEvent.Loading event)
    {
        SyncConfigValues(event);
    }

    @SubscribeEvent
    public static void OnConfigLoading(ModConfigEvent.Reloading event)
    {
        SyncConfigValues(event);
    }


//    // a list of strings that are treated as resource locations for items
//    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
//            .comment("A list of items to log on common setup.")
//            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);
//
//
//
//    private static boolean validateItemName(final Object obj) {
//        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
//    }
}
