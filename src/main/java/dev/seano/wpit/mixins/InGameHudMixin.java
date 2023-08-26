package dev.seano.wpit.mixins;

import dev.seano.wpit.WPITMod;
import dev.seano.wpit.hud.Tooltip;
import dev.seano.wpit.utils.UserCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * If our HUD can be displayed. Based upon the game HUD being hidden or the debug menu being open.
     *
     * @return True, if we should display the HUD.
     */
    @Unique
    public boolean canDisplay() {
        return !this.client.options.debugEnabled && !this.client.options.hudHidden;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void wpit$render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (canDisplay()) {
            Optional<Entity> entity = WPITMod.RAY_TRACING.getTarget();
            if (entity.isPresent() && (entity.get() instanceof TameableEntity || entity.get() instanceof FoxEntity)) {
                this.client.getProfiler().push("wpit tooltip");

                List<Text> texts = new ArrayList<>();
                texts.add(Text.translatable(entity.get().getType().getTranslationKey()));

                List<UUID> owners = WPITMod.TAMEABLE_HELPER.getEntityOwners(entity.get());
                if (!owners.isEmpty()) {
                    owners.stream().filter(Objects::nonNull).map(UserCache::getProfile).filter(Optional::isPresent).forEach(gameProfile -> texts.add(Text.of(String.format("%s", gameProfile.get().getName()))));
                }

                new Tooltip(client, texts.toArray(Text[]::new)).render(context);

                this.client.getProfiler().pop();
            }
        }
    }
}
