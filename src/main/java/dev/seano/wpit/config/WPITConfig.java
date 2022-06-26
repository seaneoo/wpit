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

    // Turn the features of the mod on or off
    public boolean enabled = true;

    // Whether the nameplate should be shown always, when you're nearby, or if you hover over the entity
    public NameplateDisplay alwaysDisplay = NameplateDisplay.ON_HOVER;

    // The size of the nameplate (relative to the default size)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int scale = 100;

    // Changes the color of the nameplate text
    public Color color = Color.WHITE;

    // Whether to display the secondary owners of an entity
    public boolean showSecondaryOwners = true;
}
