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
import dev.seano.wpit.config.NameplateDisplay;
import dev.seano.wpit.config.WPITConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

/**
 * @author Sean O'Connor
 */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Shadow
    @Final
    private TextRenderer textRenderer;

    /**
     * Returns a list of UUIDs for players who are "trusted" by the specified Fox entity.
     *
     * @param entity Instance of the Fox entity
     * @return List of UUIDs
     */
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

    /**
     * Returns a list of UUIDs for players who "own" the specified entity.
     *
     * @param entity Instance of the entity
     * @return List of UUIDs
     */
    private List<UUID> getEntityOwners(T entity) {
        if (entity instanceof Tameable e) { // Wolf, Cat, Parrot
            if (e.getOwnerUuid() != null) return Collections.singletonList(e.getOwnerUuid());
        } else if (entity instanceof AbstractHorseEntity e) { // Horse, Donkey, Mule, Llama
            if (e.isTame()) return Collections.singletonList(e.getOwnerUuid());
        } else if (entity instanceof FoxEntity e) { // Fox
            return getFoxTrustedUuids(e);
        }
        return Collections.emptyList();
    }

    /**
     * Custom implementation of {@link EntityRenderer#renderLabelIfPresent(Entity, Text, MatrixStack, VertexConsumerProvider, int)} that determines when, and
     * how, a nameplate should be displayed above the entity.
     *
     * @param entity          Instance of the entity from {@link EntityRenderer#render(Entity, float, float, MatrixStack, VertexConsumerProvider, int)}
     * @param matrixStack     Instance of the MatrixStack from {@link EntityRenderer#render(Entity, float, float, MatrixStack, VertexConsumerProvider, int)}
     * @param vertexConsumers Instance of the VertexConsumerProvider from
     *                        {@link EntityRenderer#render(Entity, float, float, MatrixStack, VertexConsumerProvider, int)}
     * @param light           Light value from {@link EntityRenderer#render(Entity, float, float, MatrixStack, VertexConsumerProvider, int)}
     */
    @SuppressWarnings("JavadocReference")
    private void drawNameplate(T entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        MinecraftClient minecraft = WPIT.getInstance()
                .getMinecraftClient();
        WPITConfig config = WPIT.getInstance()
                .getConfig();

        if (entity instanceof Tameable || entity instanceof AbstractHorseEntity || entity instanceof FoxEntity) {
            // mod is not enabled, do not render
            if (!config.modEnabled) return;

            // game hud is hidden, do not render
            if (minecraft.options.hudHidden) return;

            // if the entity is not targeted, do not render
            if (dispatcher.targetedEntity != entity && config.displayMode == NameplateDisplay.ON_HOVER) return;

            // player is a passenger on the entity, do not render
            if (entity.hasPassenger(minecraft.player)) return;

            List<UUID> uuids = getEntityOwners(entity);
            if (uuids.isEmpty()) return; // no players to render

            for (int i = uuids.size() - 1; i >= 0; i--) {
                // skip if non existent
                if (uuids.get(i) == null) return;

                Optional<GameProfile> gameProfile = UserCacheManager.getProfile(uuids.get(i));
                // skip if profile is not found
                if (gameProfile.isEmpty()) return;

                String nameStr = gameProfile.get()
                        .getName();
                Text nameplateText = Text.of(config.nameplateFormat.formatted(nameStr));

                double dis = dispatcher.getSquaredDistanceToCamera(entity);

                // if mode is NEARBY, use num of blocks from config, otherwise default to 64 blocks
                @SuppressWarnings("SwitchStatementWithTooFewBranches") int numBlocks = switch (config.displayMode) {
                    case NEARBY -> config.nearbyDistance;
                    default -> 64;
                };

                // do not render if more than numBlocks blocks away
                if (dis <= numBlocks * numBlocks) {
                    float translateY = entity.hasCustomName() ? .75f : .5f;
                    float scale = .025f;
                    float posY = -10 * i;

                    matrixStack.push();
                    matrixStack.translate(0, entity.getHeight() + translateY, 0);
                    matrixStack.multiply(dispatcher.getRotation());
                    matrixStack.scale(-scale, -scale, scale);

                    Matrix4f positionMatrix = matrixStack.peek()
                            .getPositionMatrix();

                    float bgOpacity = minecraft.options.getTextBackgroundOpacity(.25f);
                    int j = (int) (bgOpacity * 255f) << 24;
                    float x = -textRenderer.getWidth(nameplateText) / 2f;

                    textRenderer.draw(nameplateText, x, posY, 0x000000, false, positionMatrix, vertexConsumers, true, j, light);
                    textRenderer.draw(nameplateText, x, posY, WPIT.getInstance()
                            .getConfig().textColor.getHexadecimal(), false, positionMatrix, vertexConsumers, false, 0, light);


                    matrixStack.pop();
                }

                if (!config.showOtherOwners && i == 0) return;
            }
        }
    }

    /**
     * Injects into the {@link EntityRenderer#render(Entity, float, float, MatrixStack, VertexConsumerProvider, int)} function to call
     * our {@link #drawNameplate(Entity, MatrixStack, VertexConsumerProvider, int)} function.
     *
     * @param entity
     * @param yaw
     * @param tickDelta
     * @param matrices
     * @param vertexConsumers
     * @param light
     * @param ci
     */
    @SuppressWarnings("JavadocDeclaration")
    @Inject(method = {"render"}, at = {@At(value = "HEAD")})
    private void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        this.drawNameplate(entity, matrices, vertexConsumers, light);
    }
}
