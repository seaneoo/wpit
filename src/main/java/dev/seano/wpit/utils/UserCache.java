package dev.seano.wpit.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import dev.seano.wpit.WPITMod;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserCache {
    private static final LoadingCache<UUID, Optional<GameProfile>> userCache;

    static {
        CacheLoader<UUID, Optional<GameProfile>> cacheLoader = new CacheLoader<>() {
            @Override
            public @NotNull Optional<GameProfile> load(@NotNull UUID uuid) {
                Runnable runnable = () -> {
                    GameProfile gameProfile = new GameProfile(uuid, null);
                    gameProfile = WPITMod.MINECRAFT_CLIENT.getSessionService().fillProfileProperties(gameProfile, false);
                    userCache.put(uuid, Optional.ofNullable(gameProfile));
                };

                CompletableFuture.runAsync(runnable);
                return Optional.empty();
            }
        };

        userCache = CacheBuilder.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).build(cacheLoader);
    }

    public static LoadingCache<UUID, Optional<GameProfile>> getUserCache() {
        return userCache;
    }

    public static Optional<GameProfile> getProfile(UUID uuid) {
        try {
            return getUserCache().get(uuid);
        } catch (ExecutionException e) {
            WPITMod.LOGGER.warn(e.getMessage());
        }
        return Optional.empty();
    }
}
