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

@Config(name = WPIT.MOD_ID)
public class WPITConfig implements ConfigData {

    public boolean modEnabled = true;

    public NameplateDisplay displayMode = NameplateDisplay.ON_HOVER;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 64)
    public int nearbyDistance = 8;

    public boolean showOtherOwners = true;

    public String nameplateFormat = "%s";

    public Color textColor = Color.WHITE;
}
