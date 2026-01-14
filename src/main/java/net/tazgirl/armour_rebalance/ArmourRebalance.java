package net.tazgirl.armour_rebalance;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ArmourRebalance.MODID)
public class ArmourRebalance
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "armour_rebalance";

    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ArmourRebalance(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
