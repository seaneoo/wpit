package dev.seano.wpit.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("SameParameterValue")
@Environment(EnvType.CLIENT)
public class Tooltip {
    private final MinecraftClient minecraftClient;
    private final List<Text> textList;

    public Tooltip(MinecraftClient minecraftClient, Text... texts) {
        this.minecraftClient = minecraftClient;
        this.textList = List.of(texts);
    }

    public void render(DrawContext drawContext) {
        Optional<Integer> maxTextWidth = textList.stream()
                .map(minecraftClient.textRenderer::getWidth).max(Integer::compare);
        int textHeight = minecraftClient.textRenderer.fontHeight;

        int scaledWindowWidth = drawContext.getScaledWindowWidth();
        int x = (scaledWindowWidth / 2) - (maxTextWidth.orElse(0) / 2);
        int y = 5;
        int h = textList.size() * textHeight;
        int z = 0;

        int tooltipX = x - 2;
        int tooltipY = y - 2;
        int tooltipW = maxTextWidth.orElse(0) + 5;
        int tooltipH = h + 4;

        for (int index = 0; index < textList.size(); index++) {
            Text text = textList.get(index);
            drawContext.drawTextWithShadow(this.minecraftClient.textRenderer, text, x + 1,
                    (y + 1) + (index * textHeight), 0xFFFFFF);
        }

        this.renderHorizontalLine(drawContext, tooltipX, tooltipY - 1, tooltipW, z, -267386864);
        this.renderHorizontalLine(drawContext, tooltipX, tooltipY + tooltipH, tooltipW, z,
                -267386864);
        this.renderRectangle(drawContext, tooltipX, tooltipY, tooltipW, tooltipH, z, -267386864);
        this.renderVerticalLine(drawContext, tooltipX - 1, tooltipY, tooltipH, z, -267386864);
        this.renderVerticalLine(drawContext, tooltipX + tooltipW, tooltipY, tooltipH, z,
                -267386864);
        this.renderBorder(drawContext, tooltipX, tooltipY + 1, tooltipW, tooltipH, z, 0x505000FF,
                1344798847);
    }

    private void renderBorder(DrawContext context, int x, int y, int width, int height, int z,
                              int startColor, int endColor) {
        this.renderVerticalLine(context, x, y, height - 2, z, startColor, endColor);
        this.renderVerticalLine(context, x + width - 1, y, height - 2, z, startColor, endColor);
        this.renderHorizontalLine(context, x, y - 1, width, z, startColor);
        this.renderHorizontalLine(context, x, y - 1 + height - 1, width, z, endColor);
    }

    private void renderVerticalLine(DrawContext context, int x, int y, int height, int z,
                                    int color) {
        context.fill(x, y, x + 1, y + height, z, color);
    }

    private void renderVerticalLine(DrawContext context, int x, int y, int height, int z,
                                    int startColor, int endColor) {
        context.fillGradient(x, y, x + 1, y + height, z, startColor, endColor);
    }

    private void renderHorizontalLine(DrawContext context, int x, int y, int width, int z,
                                      int color) {
        context.fill(x, y, x + width, y + 1, z, color);
    }

    private void renderRectangle(DrawContext context, int x, int y, int width, int height, int z,
                                 int color) {
        context.fill(x, y, x + width, y + height, z, color);
    }
}