/*
 * This is free and unencumbered software released into the public domain.
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 */

package dev.seano.wpit.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Sean O'Connor
 */
@Mixin(FoxEntity.class)
public interface FoxEntityInvoker {

    @Accessor("OWNER")
    TrackedData<Optional<UUID>> getOwnerData();

    @Accessor("OTHER_TRUSTED")
    TrackedData<Optional<UUID>> getOtherTrustedData();
}
