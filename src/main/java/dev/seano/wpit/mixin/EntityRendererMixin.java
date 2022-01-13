/*
 * Copyright (c) 2022 Sean O'Connor. All rights reserved.
 * Licensed under the BSD 3-Clause "New" or "Revised" License.
 * https://spdx.org/licenses/BSD-3-Clause.html
 */

package dev.seano.wpit.mixin;

import com.mojang.authlib.GameProfile;
import dev.seano.wpit.UserCacheManager;
import dev.seano.wpit.WPIT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    private UUID getEntityOwner(T entity) {
        if (entity instanceof TameableEntity e) { // Wolf, Cat
            if (e.isTamed()) return e.getOwnerUuid();
        } else if (entity instanceof HorseBaseEntity e) { // Horse, Donkey, Mule, Llama
            if (e.isTame()) return e.getOwnerUuid();
        }
        return null;
    }

    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Shadow
    @Final
    private TextRenderer textRenderer;

    // Render a nameplate for the name of the entity's owner
    @Inject(method = {"render"}, at = {@At(value = "HEAD")})
    private void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof TameableEntity || entity instanceof HorseBaseEntity) {
            // Do not render if the mod is disabled
            if (!WPIT.getInstance().getConfig().enabled) return;
            // Do not render nameplate if hud is hidden
            if (WPIT.minecraft.options.hudHidden) return;
            // Do not render if the targetedEntity is not the entity
            if (dispatcher.targetedEntity != entity && !WPIT.getInstance().getConfig().alwaysDisplay) return;

            UUID ownerUuid = getEntityOwner(entity);
            // Do not render if the owner is not found
            if (ownerUuid == null) return;

            Optional<GameProfile> owner = UserCacheManager.getProfile(ownerUuid);
            // Do not render if profile is not found
            if (owner.isEmpty()) return;

            Text text = new TranslatableText("wpit.text.nameplate", owner.get().getName());

            double dis = dispatcher.getSquaredDistanceToCamera(entity);
            // Only render if player (dispatcher) is less than, or equal to, 64 blocks away
            if (dis <= 4096D) {
                float y = entity.hasCustomName() ? 0.75F : 0.5F;
                float scale = 0.025F * (WPIT.getInstance().getConfig().scale / 100F);

                matrices.push();
                matrices.translate(0, entity.getHeight() + y, 0);
                matrices.multiply(dispatcher.getRotation());
                matrices.scale(-scale, -scale, scale);

                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                float bgOpacity = WPIT.minecraft.options.getTextBackgroundOpacity(0.25F);
                int j = (int) (bgOpacity * 255.0f) << 24;
                float h = -textRenderer.getWidth(text) / 2F;
                textRenderer.draw(text, h, 0, 0x20FFFFFF, false, matrix4f, vertexConsumers, true, j, light);
                textRenderer.draw(text, h, 0, WPIT.getInstance().getConfig().color.getHexadecimal(), false, matrix4f, vertexConsumers, false, 0, light);

                matrices.pop();
            }
        }
    }
}
