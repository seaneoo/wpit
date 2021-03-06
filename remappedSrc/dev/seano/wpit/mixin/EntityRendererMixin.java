/*
 * This is free and unencumbered software released into the public domain.
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 */

package dev.seano.wpit.mixin;

import com.google.common.collect.Lists;
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
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    // Custom implementation of FoxEntity:getTrustedUuids since I couldn't get it to work :-(
    private List<UUID> getFoxTrustedUuids(FoxEntity entity) {
        ArrayList<UUID> list = Lists.newArrayList();
        list.add(entity.getDataTracker()
                .get(((FoxEntityInvoker) entity).getOwnerData())
                .orElse(null));
        list.add(entity.getDataTracker()
                .get(((FoxEntityInvoker) entity).getOtherTrustedData())
                .orElse(null));
        return list;
    }

    private List<UUID> getEntityOwners(T entity) {
        if (entity instanceof TameableEntity e) { // Wolf, Cat, Parrot
            if (e.isTamed()) return Collections.singletonList(e.getOwnerUuid());
        } else if (entity instanceof AbstractHorseEntity e) { // Horse, Donkey, Mule, Llama
            if (e.isTame()) return Collections.singletonList(e.getOwnerUuid());
        } else if (entity instanceof FoxEntity e) { // Fox
            return getFoxTrustedUuids(e);
        }
        return Collections.emptyList();
    }

    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Shadow
    @Final
    private TextRenderer textRenderer;

    // Render a nameplate for the name of the entity's owner
    @Inject(method = {"render"}, at = {@At(value = "HEAD")})
    private void render(T entity, float yaw, float tickDelta, MatrixStack matrices,
                        VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof TameableEntity || entity instanceof AbstractHorseEntity || entity instanceof FoxEntity) {
            // Do not render if the mod is disabled
            if (!WPIT.getInstance()
                    .getConfig().enabled) return;
            // Do not render nameplate if hud is hidden
            if (WPIT.minecraft.options.hudHidden) return;
            // Do not render if the targetedEntity is not the entity
            if (dispatcher.targetedEntity != entity && !WPIT.getInstance()
                    .getConfig().alwaysDisplay) return;
            // Do not render if the player is a passenger on the entity
            if (entity.hasPassenger(WPIT.minecraft.player)) return;

            List<UUID> uuids = getEntityOwners(entity);
            // Do not render if no owners are found
            if (uuids.isEmpty()) return;

            for (int i = 0; i < uuids.size(); i++) {
                // Return if the element in the array is null
                if (uuids.get(i) == null) return;
                Optional<GameProfile> owner = UserCacheManager.getProfile(uuids.get(i));
                // Do not render if profile is not found
                if (owner.isEmpty()) return;

                Text text = Text.translatable("wpit.text.nameplate", owner.get()
                        .getName());

                double dis = dispatcher.getSquaredDistanceToCamera(entity);
                // Only render if player (dispatcher) is less than, or equal to, 64 blocks away
                if (dis <= 4096D) {
                    float translateY = entity.hasCustomName() ? 0.75F : 0.5F;
                    float scale = 0.025F * (WPIT.getInstance()
                            .getConfig().scale / 100F);
                    float posY = -10 * i;

                    matrices.push();
                    matrices.translate(0, entity.getHeight() + translateY, 0);
                    matrices.multiply(dispatcher.getRotation());
                    matrices.scale(-scale, -scale, scale);

                    Matrix4f matrix4f = matrices.peek()
                            .getPositionMatrix();
                    float bgOpacity = WPIT.minecraft.options.getTextBackgroundOpacity(0.25F);
                    int j = (int) (bgOpacity * 255.0f) << 24;
                    float h = -textRenderer.getWidth(text) / 2F;
                    textRenderer.draw(text, h, posY, 0x20FFFFFF, false, matrix4f, vertexConsumers, true, j, light);
                    textRenderer.draw(text, h, posY, WPIT.getInstance()
                            .getConfig().color.getHexadecimal(), false, matrix4f, vertexConsumers, false, 0, light);

                    matrices.pop();
                }

                // Do not render more the one nameplate
                if (!WPIT.getInstance()
                        .getConfig().showSecondaryOwners && i == 0) return;
            }
        }
    }
}
