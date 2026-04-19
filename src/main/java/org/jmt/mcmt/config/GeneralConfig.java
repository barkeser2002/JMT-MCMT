package org.jmt.mcmt.config;

import net.minecraftforge.common.config.Config;
import org.jmt.mcmt.MCMT;

@Config(modid = MCMT.MODID, name = "jmt_mcmt")
public class GeneralConfig {

    @Config.Name("disableAll")
    @Config.Comment("Globally disable all toggleable functionality")
    public static boolean disabled = false;

    @Config.Name("paraMax")
    @Config.Comment("Thread count config; Values <=1 are treated as 'all cores'")
    public static int paraMax = -1;

    @Config.Name("disableWorld")
    @Config.Comment("Disable world parallelisation")
    public static boolean disableWorld = false;

    @Config.Name("disableEntity")
    @Config.Comment("Disable entity parallelisation")
    public static boolean disableEntity = false;

    @Config.Name("disableTileEntity")
    @Config.Comment("Disable tile entity parallelisation")
    public static boolean disableTileEntity = false;

    @Config.Name("disableEnvironment")
    @Config.Comment("Disable environment (plant ticks, etc.) parallelisation")
    public static boolean disableEnvironment = false;

    @Config.Name("disableChunkProvider")
    @Config.Comment("Disable parallelised chunk caching")
    public static boolean disableChunkProvider = false;

    @Config.Name("chunkLockModded")
    @Config.Comment("Use chunklocks for any unknown (i.e. modded) tile entities")
    public static boolean chunkLockModded = true;

    @Config.Name("opsTracing")
    @Config.Comment("Enable tracing ops")
    public static boolean opsTracing = false;

    public static int getParallelism() {
        return paraMax <= 1 ? Runtime.getRuntime().availableProcessors() : paraMax;
    }
}
