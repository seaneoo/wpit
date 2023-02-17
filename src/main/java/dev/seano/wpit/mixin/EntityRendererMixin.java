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

        // If the entity is an instance of Tameable, AbstractHorseEntity, FoxEntity (i.e., something that can have owners)
        if (entity instanceof Tameable || entity instanceof AbstractHorseEntity || entity instanceof FoxEntity) {
            // If the mod is not enabled, do not render
            if (!config.modEnabled) return;

            // If the game hud is hidden, do not render
            if (minecraft.options.hudHidden) return;

            // If the displayMode is set to NameplateDisplay ON_HOVER and the dispatcher is not targeting the entity, do not render
            if (dispatcher.targetedEntity != entity && config.displayMode == NameplateDisplay.ON_HOVER) return;

            // If the client player is a passenger on the entity, do not render
            if (entity.hasPassenger(minecraft.player)) return;

            // Get the UUIDS for the entity
            List<UUID> uuids = getEntityOwners(entity);
            // If there are no UUIDs, do not render
            if (uuids.isEmpty()) return;

            // Loop through all the UUIDs for the entity
            for (int i = uuids.size() - 1; i >= 0; i--) {
                // If the UUID is null, skip
                if (uuids.get(i) == null) return;

                // Get the GameProfile from the UUID
                Optional<GameProfile> gameProfile = UserCacheManager.getProfile(uuids.get(i));
                // If the profile is not found, skip
                if (gameProfile.isEmpty()) return;

                // Get the player's name from the profile
                String nameStr = gameProfile.get()
                        .getName();
                // Create the text to be displayed on the nameplate, using the format specified from nameplateFormat
                Text nameplateText = Text.of(config.nameplateFormat.formatted(nameStr));

                // Get the squared distance from the dispatcher to the entity
                double dis = dispatcher.getSquaredDistanceToCamera(entity);

                // If the displayMode is set to NameplateDisplay NEARBY, use the configured nearbyDistance amount, otherwise use 64
                int numBlocks = config.displayMode == NameplateDisplay.NEARBY ? config.nearbyDistance : 64;

                // If the dispatcher's distance to the entity is <= to the numBlocks amount, render the nameplate
                if (dis <= numBlocks * numBlocks) {
                    // How far up the entity the nameplate should be, taking into account if the entity has a custom name
                    float translateY = entity.hasCustomName() ? .75f : .5f;
                    // The scale of the nameplate
                    float scale = .025f;
                    // The Y position of the nameplate, changes depending on how many player names need to be displayed
                    float posY = -10 * i;

                    // Push the matrix stack (allow changes)
                    matrixStack.push();

                    // Modify (translate, add rotations, and scale) the matrix stack
                    matrixStack.translate(0, entity.getHeight() + translateY, 0);
                    matrixStack.multiply(dispatcher.getRotation());
                    matrixStack.scale(-scale, -scale, scale);

                    // Get the position matrix from the matrix stack
                    Matrix4f positionMatrix = matrixStack.peek()
                            .getPositionMatrix();

                    // Draw the nameplate(s)
                    float bgOpacity = minecraft.options.getTextBackgroundOpacity(.25f);
                    int j = (int) (bgOpacity * 255f) << 24;
                    float x = -textRenderer.getWidth(nameplateText) / 2f;

                    textRenderer.draw(nameplateText, x, posY, 0x000000, false, positionMatrix, vertexConsumers, true, j, light);
                    textRenderer.draw(nameplateText, x, posY, WPIT.getInstance()
                            .getConfig().textColor.getHexadecimal(), false, positionMatrix, vertexConsumers, false, 0, light);

                    // Pop the matrix stack (prevent further changes)
                    matrixStack.pop();
                }

                // If showOtherOwners is set to false and the loop is on its first iteration, break from the loop
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
