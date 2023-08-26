package dev.seano.wpit.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Objects;
import java.util.Optional;

public class RayTracing {

    public Entity target = null;

    public RayTracing() {
    }

    public Optional<Entity> getTarget() {
        return Optional.ofNullable(this.target);
    }

    public void fire(MinecraftClient minecraftClient) {
        HitResult hitResult = minecraftClient.crosshairTarget;
        if (hitResult != null) {
            if (Objects.requireNonNull(hitResult.getType()) == HitResult.Type.ENTITY) {
                target = ((EntityHitResult) hitResult).getEntity();
            } else {
                target = null;
            }
        }
    }
}
