package dev.seano.wpit.mixins;

import dev.seano.wpit.WPITMod;
import dev.seano.wpit.hud.Tooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

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
            if (entity.isPresent() && entity.get() instanceof LivingEntity) {
                this.client.getProfiler().push("wpit tooltip");

                Text text0 = Text.translatable(entity.get().getType().getTranslationKey());
                Text text1 = Text.of("Lorem Ipsum");
                new Tooltip(client, text0, text1).render(context);

                this.client.getProfiler().pop();
            }
        }
    }
}
