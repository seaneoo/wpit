package dev.seano.wpit.mixins;

import dev.seano.wpit.WPITMod;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("HEAD"))
    public void wpit$render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof LivingEntity) {
            double squaredDistanceToCamera = this.dispatcher.getSquaredDistanceToCamera(entity);
            if (squaredDistanceToCamera > 4096.0) return;

            Text text = Text.translatable(entity.getType().getTranslationKey());

            matrices.push();
            matrices.translate(0f, entity.getNameLabelHeight(), 0f);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.025f, -0.025f, 0.025f);

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float g = WPITMod.MINECRAFT_CLIENT.options.getTextBackgroundOpacity(0.25f);
            int j = (int) (g * 255.0f) << 24;
            TextRenderer textRenderer = this.getTextRenderer();
            float h = (float) -textRenderer.getWidth(text) / 2;
            this.getTextRenderer().draw(text, h, 0, 0x20FFFFFF, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, j, light);
            this.getTextRenderer().draw(text, h, 0, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

            matrices.pop();
        }
    }
}