package org.jmt.mcmt.asmdest;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmt.mcmt.MCMT;
import org.jmt.mcmt.config.GeneralConfig;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;

public class ASMHookTerminator {

    private static final Logger LOGGER = LogManager.getLogger();

    static Phaser p;
    static ExecutorService ex;

    public static AtomicInteger currentWorlds = new AtomicInteger(0);
    public static AtomicInteger currentEnts = new AtomicInteger(0);
    public static AtomicInteger currentTEs = new AtomicInteger(0);
    public static AtomicInteger currentEnvs = new AtomicInteger(0);

    public static AtomicBoolean isTicking = new AtomicBoolean(false);

    public static Set<String> currentTasks = ConcurrentHashMap.newKeySet();

    private static long tickStart = 0;

    public static void setupThreadpool(int paralellism) {
        final ClassLoader cl = ASMHookTerminator.class.getClassLoader();
        ForkJoinWorkerThreadFactory fjwtf = new ForkJoinWorkerThreadFactory() {
            @Override
            public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                ForkJoinWorkerThread fjwt = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                fjwt.setName("MCMT-Pool-Thread-" + fjwt.getPoolIndex());
                fjwt.setContextClassLoader(cl);
                return fjwt;
            }
        };
        ex = new ForkJoinPool(
                paralellism,
                fjwtf,
                null, false);
    }

    public static void preTick(MinecraftServer server) {
        if (p != null) {
            LOGGER.warn("Multiple servers?");
            return;
        } else {
            isTicking.set(true);
            p = new Phaser();
            p.register();
            tickStart = System.nanoTime();
        }
    }

    public static boolean callTick(WorldServer serverworld) {
        if (GeneralConfig.disabled || GeneralConfig.disableWorld) {
            return false;
        }

        if (p == null) {
            setupThreadpool(GeneralConfig.getParallelism());
            preTick(serverworld.getMinecraftServer());
        }

        String taskName = null;
        if (GeneralConfig.opsTracing) {
            taskName = "WorldTick: " + serverworld.toString() + "@" + serverworld.hashCode();
            currentTasks.add(taskName);
        }
        String finalTaskName = taskName;
        p.register();
        ex.execute(() -> {
            try {
                currentWorlds.incrementAndGet();
                serverworld.tick();
            } finally {
                p.arriveAndDeregister();
                currentWorlds.decrementAndGet();
                if (GeneralConfig.opsTracing) currentTasks.remove(finalTaskName);
            }
        });

        return true;
    }

    public static boolean callEntityTick(World world) {
        if (GeneralConfig.disabled || GeneralConfig.disableEntity) {
            return false;
        }

        if (p == null) {
            return false;
        }

        String taskName = null;
        if (GeneralConfig.opsTracing) {
            taskName = "EntityTick: " + world.toString() + "@" + world.hashCode();
            currentTasks.add(taskName);
        }
        String finalTaskName = taskName;
        p.register();
        ex.execute(() -> {
            try {
                currentEnts.incrementAndGet();
                world.updateEntities();
            } finally {
                currentEnts.decrementAndGet();
                p.arriveAndDeregister();
                if (GeneralConfig.opsTracing) currentTasks.remove(finalTaskName);
            }
        });
        return true;
    }

    public static boolean callChunkProviderTick(ChunkProviderServer chunkProviderServer) {
        if (GeneralConfig.disabled || GeneralConfig.disableChunkProvider) {
            return false;
        }

        if (p == null) {
            return false;
        }

        String taskName = null;
        if (GeneralConfig.opsTracing) {
            taskName = "ChunkProviderTick: " + chunkProviderServer.toString() + "@" + chunkProviderServer.hashCode();
            currentTasks.add(taskName);
        }
        String finalTaskName = taskName;
        p.register();
        ex.execute(() -> {
            try {
                chunkProviderServer.tick();
            } finally {
                p.arriveAndDeregister();
                if (GeneralConfig.opsTracing) currentTasks.remove(finalTaskName);
            }
        });
        return true;
    }

    public static long[] lastTickTime = new long[32];
    public static int lastTickTimePos = 0;
    public static int lastTickTimeFill = 0;

    public static void postTick(MinecraftServer server) {
        if (p != null) {
            p.arriveAndAwaitAdvance();
            isTicking.set(false);
            p = null;
            lastTickTime[lastTickTimePos] = System.nanoTime() - tickStart;
            lastTickTimePos = (lastTickTimePos + 1) % lastTickTime.length;
            lastTickTimeFill = Math.min(lastTickTimeFill + 1, lastTickTime.length - 1);
        }
    }
}
