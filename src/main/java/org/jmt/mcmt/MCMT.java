package org.jmt.mcmt;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MCMT.MODID, name = MCMT.NAME, version = MCMT.VERSION, acceptableRemoteVersions = "*")
public class MCMT {
    public static final String MODID = "jmt_mcmt";
    public static final String NAME = "JMT MCMT";
    public static final String VERSION = "0.23.89-PRE";

    private static final Logger LOGGER = LogManager.getLogger();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("MCMT Pre-Init");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("MCMT Init");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        LOGGER.info("MCMT Server Starting");
    }
}
