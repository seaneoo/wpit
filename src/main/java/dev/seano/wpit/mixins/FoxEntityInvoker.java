package dev.seano.wpit.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(FoxEntity.class)
public interface FoxEntityInvoker {

    /**
     * Allows the FoxEntity OWNER tracked data to be accessed.
     */
    @Accessor("OWNER")
    static TrackedData<Optional<UUID>> getOwnerData() {
        throw new AssertionError();
    }

    /**
     * Allows the FoxEntity OTHER_TRUSTED tracked data to be accessed.
     */
    @Accessor("OTHER_TRUSTED")
    static TrackedData<Optional<UUID>> getOtherTrustedData() {
        throw new AssertionError();
    }
}
