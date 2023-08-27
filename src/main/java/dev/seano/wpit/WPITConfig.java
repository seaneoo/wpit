package dev.seano.wpit;

import eu.midnightdust.lib.config.MidnightConfig;

public class WPITConfig extends MidnightConfig {

    @Entry(category = "text")
    public static DisplayMode mode = DisplayMode.TOOLTIP;

    @Entry(category = "text")
    public static TooltipPosition position = TooltipPosition.TOP_CENTER;

    public enum DisplayMode {
        TOOLTIP, NAMEPLATE
    }

    public enum TooltipPosition {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT
    }
}
