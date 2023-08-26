package dev.seano.wpit.mixins;

import dev.seano.wpit.WPITMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    public void wpit$render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (!this.client.options.debugEnabled && !this.client.options.hudHidden) {
            Entity entity = WPITMod.RAY_TRACING.target;
            if (entity != null) {
                Text text = Text.translatable(entity.getType().getTranslationKey());
                int x = (context.getScaledWindowWidth() / 2) - (this.getTextRenderer().getWidth(text) / 2);

                context.drawTextWithShadow(this.getTextRenderer(), text, x, 10, 0xffffff);
                // TODO: Render WPIT GUI
            }
        }
    }
}
