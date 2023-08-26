package dev.seano.wpit;

import eu.midnightdust.lib.config.MidnightConfig;

public class WPITConfig extends MidnightConfig {

    @Entry(category = "text")
    public static DisplayMode mode = DisplayMode.TOOLTIP;

    public enum DisplayMode {
        TOOLTIP, NAMEPLATE
    }
}
