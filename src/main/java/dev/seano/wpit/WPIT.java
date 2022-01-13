package dev.seano.wpit;

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

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        logger = LogManager.getLogger(ID);
    }

    public static WPIT getInstance() {
        return INSTANCE;
    }

    public Logger getLogger() {
        return logger;
    }
}
