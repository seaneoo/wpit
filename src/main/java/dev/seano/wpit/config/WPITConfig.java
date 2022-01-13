/*
 * Copyright (c) 2022 Sean O'Connor. All rights reserved.
 * Licensed under the BSD 3-Clause "New" or "Revised" License.
 * https://spdx.org/licenses/BSD-3-Clause.html
 */

package dev.seano.wpit.config;

import dev.seano.wpit.WPIT;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = WPIT.ID)
public class WPITConfig implements ConfigData {

    // Enable or disable the mod
    public boolean enabled = true;

    // Display the nameplates, even if you aren't looking at the entity
    public boolean alwaysDisplay = false;

    // Size of the nameplate (relative to the default scale)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int scale = 100;
}
