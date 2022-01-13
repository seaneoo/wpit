/*
 * Copyright (c) 2022 Sean O'Connor. All rights reserved.
 * Licensed under the BSD 3-Clause "New" or "Revised" License.
 * https://spdx.org/licenses/BSD-3-Clause.html
 */

package dev.seano.wpit.config;

public enum Color {

    BLACK(0x000000),
    DARK_BLUE(0x0000AA),
    DARK_GREEN(0x00AA00),
    DARK_AQUA(0x00AAAA),
    DARK_RED(0xAA0000),
    DARK_PURPLE(0xAA00AA),
    GOLD(0xFFAA00),
    GRAY(0xAAAAAA),
    DARK_GRAY(0x555555),
    BLUE(0x5555FF),
    GREEN(0x55FF55),
    AQUA(0x55FFFF),
    RED(0xFF5555),
    LIGHT_PURPLE(0xFF55FF),
    YELLOW(0xFFFF55),
    WHITE(0xFFFFFF);

    private final int hexadecimal;

    Color(int hexadecimal) {
        this.hexadecimal = hexadecimal;
    }

    public int getHexadecimal() {
        return hexadecimal;
    }
}
