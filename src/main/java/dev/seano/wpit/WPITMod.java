package dev.seano.wpit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class WPITMod implements ClientModInitializer {
    //    public static final String MOD_ID = "wpit";
    public static final String MOD_NAME = "WPIT";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initialize client");
    }
}