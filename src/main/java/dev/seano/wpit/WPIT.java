/*
 * This is free and unencumbered software released into the public domain.
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 */

package dev.seano.wpit;

import dev.seano.wpit.config.WPITConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class WPIT implements ClientModInitializer {
    public static final String MOD_ID = "wpit";
    public static final String MOD_NAME = "WPIT";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    private static WPIT INSTANCE;
    private WPITConfig config;
    private MinecraftClient minecraftClient;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        AutoConfig.register(WPITConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(WPITConfig.class)
                .getConfig();

        minecraftClient = MinecraftClient.getInstance();
    }

    public static WPIT getInstance() {
        return INSTANCE;
    }

    public WPITConfig getConfig() {
        return config;
    }

    public MinecraftClient getMinecraftClient() {
        return minecraftClient;
    }
}
