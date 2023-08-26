package dev.seano.wpit.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
@Environment(EnvType.CLIENT)
public class Tooltip {
    private final MinecraftClient minecraftClient;
    private final @Nullable Text text;

    public Tooltip(MinecraftClient minecraftClient, @Nullable Text text) {
        this.minecraftClient = minecraftClient;
        this.text = text;
    }

    public void render(DrawContext drawContext) {
        int textWidth = minecraftClient.textRenderer.getWidth(this.text);
        int textHeight = minecraftClient.textRenderer.fontHeight;

        int scaledWindowWidth = drawContext.getScaledWindowWidth();
        int x = (scaledWindowWidth / 2) - (textWidth / 2);
        int y = 5;
        int z = 0;

        int i = x - 2;
        int j = y - 2;
        int k = textWidth + 5;
        int l = textHeight + 4;

        drawContext.drawTextWithShadow(this.minecraftClient.textRenderer, this.text, x + 1, y + 1, 0xFFFFFF);
        this.renderHorizontalLine(drawContext, i, j - 1, k, z, -267386864);
        this.renderHorizontalLine(drawContext, i, j + l, k, z, -267386864);
        this.renderRectangle(drawContext, i, j, k, l, z, -267386864);
        this.renderVerticalLine(drawContext, i - 1, j, l, z, -267386864);
        this.renderVerticalLine(drawContext, i + k, j, l, z, -267386864);
        this.renderBorder(drawContext, i, j + 1, k, l, z, 0x505000FF, 1344798847);
    }

    private void renderBorder(DrawContext context, int x, int y, int width, int height, int z, int startColor, int endColor) {
        this.renderVerticalLine(context, x, y, height - 2, z, startColor, endColor);
        this.renderVerticalLine(context, x + width - 1, y, height - 2, z, startColor, endColor);
        this.renderHorizontalLine(context, x, y - 1, width, z, startColor);
        this.renderHorizontalLine(context, x, y - 1 + height - 1, width, z, endColor);
    }

    private void renderVerticalLine(DrawContext context, int x, int y, int height, int z, int color) {
        context.fill(x, y, x + 1, y + height, z, color);
    }

    private void renderVerticalLine(DrawContext context, int x, int y, int height, int z, int startColor, int endColor) {
        context.fillGradient(x, y, x + 1, y + height, z, startColor, endColor);
    }

    private void renderHorizontalLine(DrawContext context, int x, int y, int width, int z, int color) {
        context.fill(x, y, x + width, y + 1, z, color);
    }

    private void renderRectangle(DrawContext context, int x, int y, int width, int height, int z, int color) {
        context.fill(x, y, x + width, y + height, z, color);
    }
}