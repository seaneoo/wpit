package dev.seano.wpit.mixins;

import dev.seano.wpit.WPITMod;
import dev.seano.wpit.hud.Tooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private Tooltip tooltip;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void wpit$init(MinecraftClient client, ItemRenderer itemRenderer, CallbackInfo ci) {
        this.tooltip = new Tooltip(client, Text.of("Apple")); // TODO: Get the entity data
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void wpit$render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (!this.client.options.debugEnabled && !this.client.options.hudHidden) {
            Entity entity = WPITMod.RAY_TRACING.target;
            if (entity != null) {
                this.client.getProfiler().push("wpit tooltip");
                this.tooltip.render(context);
                this.client.getProfiler().pop();
            }
        }
    }
}
