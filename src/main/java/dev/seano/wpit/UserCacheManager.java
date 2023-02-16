/*
 * This is free and unencumbered software released into the public domain.
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 */

package dev.seano.wpit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserCacheManager {

    private static final LoadingCache<UUID, Optional<GameProfile>> userCache;

    static {
        userCache = CacheBuilder.newBuilder()
                .expireAfterWrite(6, TimeUnit.HOURS)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull Optional<GameProfile> load(@NotNull UUID uuid) {
                        CompletableFuture.runAsync(() -> {
                            GameProfile gameProfile = new GameProfile(uuid, null);
                            gameProfile = WPIT.getInstance()
                                    .getMinecraftClient()
                                    .getSessionService()
                                    .fillProfileProperties(gameProfile, false);
                            userCache.put(uuid, Optional.ofNullable(gameProfile));
                        });
                        return Optional.empty();
                    }
                });
    }

    public static LoadingCache<UUID, Optional<GameProfile>> getUserCache() {
        return userCache;
    }

    public static Optional<GameProfile> getProfile(UUID uuid) {
        try {
            return getUserCache().get(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
