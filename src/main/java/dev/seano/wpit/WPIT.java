/*
 * Copyright (c) 2022 Sean O'Connor. All rights reserved.
 * Licensed under the BSD 3-Clause "New" or "Revised" License.
 * https://spdx.org/licenses/BSD-3-Clause.html
 */

package dev.seano.wpit;

import dev.seano.wpit.config.WPITConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class WPIT implements ClientModInitializer {

    public static final String ID = "wpit";

    private static WPIT INSTANCE;
    private Logger logger;
    private WPITConfig config;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        logger = LogManager.getLogger(ID);

        AutoConfig.register(WPITConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(WPITConfig.class).getConfig();
    }

    public static WPIT getInstance() {
        return INSTANCE;
    }

    public Logger getLogger() {
        return logger;
    }

    public WPITConfig getConfig() {
        return config;
    }
}
