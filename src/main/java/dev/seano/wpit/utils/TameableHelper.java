package dev.seano.wpit.utils;

import com.google.common.collect.Lists;
import dev.seano.wpit.WPITMod;
import dev.seano.wpit.mixins.FoxEntityInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.Text;

import java.util.*;

public class TameableHelper {
    public TameableHelper() {
    }

    private List<UUID> getFoxTrusted(FoxEntity foxEntity) {
        ArrayList<UUID> uuids = Lists.newArrayList();
        uuids.add(foxEntity.getDataTracker().get(((FoxEntityInvoker) foxEntity).getOwnerData())
                .orElse(null));
        uuids.add(foxEntity.getDataTracker()
                .get(((FoxEntityInvoker) foxEntity).getOtherTrustedData()).orElse(null));
        return uuids;
    }

    public List<UUID> getEntityOwners(Entity entity) {
        if (entity instanceof TameableEntity tameable) { // Wolves, Cats, Parrots
            if (tameable.getOwnerUuid() != null)
                return Collections.singletonList(tameable.getOwnerUuid());
        } else if (entity instanceof FoxEntity foxEntity) { // Foxes
            return getFoxTrusted(foxEntity);
//        } else if (entity instanceof AllayEntity allayEntity) { // Allays
//            var likedPlayer = allayEntity.getBrain().getOptionalRegisteredMemory
//            (MemoryModuleType.LIKED_PLAYER);
//            if (likedPlayer.isPresent()) {
//                return Collections.singletonList(likedPlayer.get());
//            }
        }
        return Collections.emptyList();
    }

    public List<String> getOwnerNames(Entity entity) {
        List<String> names = new ArrayList<>();
        List<UUID> owners = getEntityOwners(entity);

        if (!owners.isEmpty()) {
            owners.stream().filter(Objects::nonNull).map(UserCache::getProfile)
                    .filter(Optional::isPresent)
                    .forEach(gameProfile -> names.add(String.format("%s", gameProfile.get()
                            .getName())));
        }

        return names;
    }
}