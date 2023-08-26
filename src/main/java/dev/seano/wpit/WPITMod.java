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
    //    public static final String MOD_ID = "wpit";
    public static final String MOD_NAME = "WPIT";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final MinecraftClient MINECRAFT_CLIENT = MinecraftClient.getInstance();

    public static final RayTracing RAY_TRACING = new RayTracing();
    public static final TameableHelper TAMEABLE_HELPER = new TameableHelper();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(RAY_TRACING::fire);
    }
}