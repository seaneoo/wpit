/*
 * This is free and unencumbered software released into the public domain.
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
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

    public Color color = Color.WHITE;
}
