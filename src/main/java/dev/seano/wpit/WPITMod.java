package dev.seano.wpit;

import dev.seano.wpit.utils.RayTracing;
import dev.seano.wpit.utils.TameableHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class WPITMod implements ClientModInitializer {
    public static final String MOD_ID = "wpit";

    public static WPITMod INSTANCE;
    public static MinecraftClient MINECRAFT;
    public static final Logger LOGGER;

    public RayTracing rayTracing;
    public TameableHelper tameableHelper;

    static {
        LOGGER = LoggerFactory.getLogger(MOD_ID);
    }

    @Override
    public void onInitializeClient() {
        if (INSTANCE == null) INSTANCE = this;
        MINECRAFT = MinecraftClient.getInstance();

        this.rayTracing = new RayTracing();
        this.tameableHelper = new TameableHelper();

        ClientTickEvents.END_CLIENT_TICK.register(this.rayTracing::fire);
    }
}