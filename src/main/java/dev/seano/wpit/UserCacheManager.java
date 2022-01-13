/*
 * Copyright (c) 2022 Sean O'Connor. All rights reserved.
 * Licensed under the BSD 3-Clause "New" or "Revised" License.
 * https://spdx.org/licenses/BSD-3-Clause.html
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

    private static final LoadingCache<UUID, Optional<String>> userCache;

    static {
        userCache = CacheBuilder.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).build(new CacheLoader<>() {
            @Override
            public @NotNull Optional<String> load(@NotNull UUID uuid) {
                CompletableFuture.runAsync(() -> {
                    GameProfile gameProfile = new GameProfile(uuid, null);
                    gameProfile = WPIT.minecraft.getSessionService().fillProfileProperties(gameProfile, false);
                    userCache.put(uuid, Optional.ofNullable(gameProfile.getName()));
                });
                return Optional.empty();
            }
        });
    }

    public static LoadingCache<UUID, Optional<String>> getUserCache() {
        return userCache;
    }

    public static Optional<String> getUsername(UUID uuid) {
        try {
            return getUserCache().get(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
