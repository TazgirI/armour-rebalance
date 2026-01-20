package net.tazgirl.armour_rebalance;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> INFO = BUILDER.comment("To see how all of these values are used and the impact of any adjustments, check the interactable graph here (all config names match their Desmos counterpart): https://www.desmos.com/calculator/98516vuxmp").define("info", "");

    public static final ModConfigSpec.ConfigValue<Double> ARMOUR_BASE_DIV = BUILDER
            .comment("Armour base div")
            .define("adiv", 9.0, (d -> d instanceof Double && (double) d > 0));

    public static final ModConfigSpec.ConfigValue<Double> ARMOUR_ROOT_MOD = BUILDER
            .comment("Modifier applied to the square root of armour")
            .define("arootmod", 1.0, (d -> d instanceof Double && (double) d > 0));



    public static final ModConfigSpec.ConfigValue<Double> ENCHANT_LEVEL_BONUS = BUILDER
            .comment("The amount of % bonus each level of protection gives you")
            .define("pa", 4.0, (d -> d instanceof Double && (double) d > 0));

    public static final ModConfigSpec.ConfigValue<Double> ENCHANT_TYPE_MODIFIER = BUILDER
            .comment("A modifier used on the level of any enchants that match the damage type")
            .define("pla", 2.0, (d -> d instanceof Double && (double) d > 0));

    public static final ModConfigSpec.ConfigValue<Double> FEATHER_FALLING_RETENTION = BUILDER
            .comment("The % of armour NOT lost per level of feather falling when fall damage attempts to set armour to 0, this can go over 100% and act as a multiplier. i.e ffretain = 20, your boots have FF III and you have 15 armour: 15 * (20 * 3 / 100) or 9")
            .define("ffretain", 20.0, (d -> d instanceof Double && (double) d > 0));



    public static final ModConfigSpec.ConfigValue<Double> DAMAGE_MODIFIER = BUILDER
            .comment("A modifier used on damage when calculating armour reduction")
            .define("dmod", 0.5, (d -> d instanceof Double && (double) d > 0));

    public static final ModConfigSpec.ConfigValue<Double> TOUGHNESS_MODIFIER = BUILDER
            .comment("A modifier used on toughness when calculating armour reduction")
            .define("tmod", 0.85, (d -> d instanceof Double && (double) d > 0));



    public static final ModConfigSpec.ConfigValue<Double> RESISTANCE_PERCENT_ARMOUR_BONUS = BUILDER
            .comment("Resistance either applies it's percentage or raw bonus per level, depending on which would be greater. Also note it is iterative so the first two levels may apply their raw bonus and the 3rd could apply it's percent bonus")
            .define("raperc", 20.0, (d -> d instanceof Double && (double) d > 0));

    public static final ModConfigSpec.ConfigValue<Double> RESISTANCE_RAW_ARMOUR_BONUS = BUILDER
            .comment("See above")
            .define("raraw", 10.0, (d -> d instanceof Double && (double) d > 0));

    public static final ModConfigSpec.ConfigValue<Double> RESISTANCE_PERCENT_TOUGHNESS_BONUS = BUILDER
            .comment("See above but toughness is iterated separately so a raw armour bonus but percentile toughness bonus could be applied")
            .define("rtperc", 15.0, (d -> d instanceof Double && (double) d > 0));

    public static final ModConfigSpec.ConfigValue<Double> RESISTANCE_RAW_TOUGHNESS_BONUS = BUILDER
            .comment("See above")
            .define("rtraw", 3.0, (d -> d instanceof Double && (double) d > 0));



    public static void SyncConfigValues()
    {
        Constants.adiv = ARMOUR_BASE_DIV.get();
        Constants.arootmod = ARMOUR_ROOT_MOD.get();

        Constants.dmod = DAMAGE_MODIFIER.get();
        Constants.tmod = TOUGHNESS_MODIFIER.get();

        Constants.pa = ENCHANT_LEVEL_BONUS.get();
        Constants.pla = ENCHANT_TYPE_MODIFIER.get();
        Constants.ffretain = FEATHER_FALLING_RETENTION.get();

        Constants.raperc = RESISTANCE_PERCENT_ARMOUR_BONUS.get();
        Constants.raraw = RESISTANCE_RAW_ARMOUR_BONUS.get();
        Constants.rtperc = RESISTANCE_PERCENT_TOUGHNESS_BONUS.get();
        Constants.rtraw = RESISTANCE_RAW_TOUGHNESS_BONUS.get();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void OnConfigLoading(ModConfigEvent.Loading event)
    {
        SyncConfigValues();
    }

    @SubscribeEvent
    public static void OnConfigLoading(ModConfigEvent.Reloading event)
    {
        SyncConfigValues();
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
