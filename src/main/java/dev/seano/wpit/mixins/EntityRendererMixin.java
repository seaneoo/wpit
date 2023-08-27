package dev.seano.wpit.mixins;

import dev.seano.wpit.WPITConfig;
import dev.seano.wpit.WPITMod;
import dev.seano.wpit.utils.UserCache;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("HEAD"))
    public void wpit$render(T entity, float yaw, float tickDelta, MatrixStack matrices,
                            VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof TameableEntity || entity instanceof FoxEntity) {
            /* If the dispatcher is > 64 blocks away from the entity, hide the nameplate. */
            double squaredDistanceToCamera = this.dispatcher.getSquaredDistanceToCamera(entity);
            if (squaredDistanceToCamera > 4096.0) return;

            /* If the dispatcher is not targeting the entity, hide the nameplate. */
            if (dispatcher.targetedEntity != entity) return;

            /* If the player is riding the entity, hide the nameplate. */
            if (entity.hasPassenger(WPITMod.MINECRAFT.player)) return;

            /* If the HUD is hidden, hide the nameplate. */
            if (WPITMod.MINECRAFT.options.hudHidden) return;

            /* If the mode is not set to NAMEPLATE. */
            if (WPITConfig.mode != WPITConfig.DisplayMode.NAMEPLATE) return;

            List<Text> texts = new ArrayList<>();
            List<String> names = WPITMod.INSTANCE.tameableHelper.getOwnerNames(entity);
            if (!names.isEmpty()) names.forEach(s -> texts.add(Text.of(s)));

            for (int i = 0; i < texts.size(); i++) {
                Text text = texts.get(i);

                matrices.push();

                matrices.translate(0f, entity.getNameLabelHeight(), 0f);
                matrices.multiply(this.dispatcher.getRotation());
                matrices.scale(-0.025f, -0.025f, 0.025f);

                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                float g = WPITMod.MINECRAFT.options.getTextBackgroundOpacity(0.25f);
                int j = (int) (g * 255.0f) << 24;
                TextRenderer textRenderer = this.getTextRenderer();
                float x = (float) -textRenderer.getWidth(text) / 2;
                float y = -10 * i;
                this.getTextRenderer()
                        .draw(text, x, y, 0x20FFFFFF, false, matrix4f, vertexConsumers,
                                TextRenderer.TextLayerType.NORMAL, j, light);
                this.getTextRenderer()
                        .draw(text, x, y, -1, false, matrix4f, vertexConsumers,
                                TextRenderer.TextLayerType.NORMAL, 0, light);

                matrices.pop();
            }
        }
    }
}