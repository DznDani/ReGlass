package restudio.reglass.client.runtime;

//#if MC >= 26
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#else
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
//#endif
import restudio.reglass.client.LiquidGlassUniforms;
import restudio.reglass.client.api.ReGlassApi;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;

/**
 * Renders the two-layer Liquid Glass experience bar (clear-glass track + tinted-glass fill).
 */
public final class XpBarGlassRenderer {
    private static final int TINT_GREEN = 0x4CE82E;
    private static final int TINT_DARK_SHADOW = 0x1E7A12;

    private XpBarGlassRenderer() {}

//#if MC >= 26
    public static void render(GuiGraphicsExtractor context, int x, int y, int width, double progress, double pulse) {
        float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();
//#else
    public static void render(DrawContext context, int x, int y, int width, double progress, double pulse) {
        float guiScale = (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
//#endif
        ReGlassConfig.XpBar xp = ReGlassConfig.INSTANCE.xpBar;
        int height = 5;
        float corner = 2.5f;

        WidgetStyle trackStyle = WidgetStyle.create()
                .tint(0x000000, xp.trackTintAlpha)
                .smoothing(0f)
                .refractionThickness(6.5f)
                .fresnelRange(20f)
                .shadow(xp.trackShadowSpread * guiScale, xp.trackShadowIntensity, 0f, 1.5f)
                .shadowColor(0x000000, 0.85f)
                .shadowBlend(GlowBlendMode.MULTIPLY)
                .layer(2);
        if (xp.style == XpBarStyle.SEGMENTED) {
            trackStyle.segments(xp.segments, xp.segmentGap, (float) width);
        }

        ReGlassApi.create(context)
                .dimensions(x, y, width, height)
                .cornerRadius(corner)
                .style(trackStyle)
                .render();

        double effectiveProgress = Math.max(0.0, Math.min(1.0, progress));
        if (xp.style == XpBarStyle.SEGMENTED && xp.snapToSegments) {
            int filledSegs = (int) Math.floor(effectiveProgress * xp.segments + 1e-6);
            if (filledSegs <= 0) {
                effectiveProgress = 0.0;
            } else if (filledSegs >= xp.segments) {
                effectiveProgress = 1.0;
            } else {
                double totalGap = Math.min(xp.segmentGap, (width / (double) xp.segments) * 0.6);
                double segW = (width - (xp.segments - 1) * totalGap) / xp.segments;
                double stride = segW + totalGap;
                effectiveProgress = ((filledSegs - 1) * stride + segW) / width;
            }
        }

        if (effectiveProgress > 0.001) {
            int fillWidth = Math.max(2, Math.min(width, (int) Math.round(effectiveProgress * width)));
            float visAlpha = (float) Math.min(1.0, effectiveProgress * 30.0);
            double rad = Math.toRadians(xp.glowAngleDeg);
            float glowDx = (float) (Math.cos(rad) * xp.glowDistance);
            float glowDy = (float) (Math.sin(rad) * xp.glowDistance);
            int glowColor = xp.glowColorCustom ? hsvToRgb(xp.glowHue, 0.85f, 0.95f) : TINT_GREEN;
            int fillShadowColor = xp.fillShadowUseTint ? TINT_DARK_SHADOW : 0x000000;
            float effectiveGlow = Math.max(0f, xp.glowIntensity) * (1.0f + 0.45f * (float) pulse) * visAlpha;

            WidgetStyle fillStyle = WidgetStyle.create()
                    .tint(TINT_GREEN, 0.72f * visAlpha)
                    .tintedGlass(1.0f)
                    .layerDepthBlend(xp.layerDepthBlend)
                    .smoothing(0f)
                    .refractionThickness(6.5f)
                    .fresnelRange(20f)
                    .fresnelFactor(38f)
                    .shadow(xp.fillShadowSpread * guiScale, xp.fillShadowIntensity * visAlpha, -xp.fillShadowOffsetX, xp.fillShadowOffsetY)
                    .shadowColor(fillShadowColor, 0.90f * visAlpha)
                    .shadowBlend(xp.fillShadowBlend)
                    .glow(effectiveGlow, xp.glowSpread + (float) pulse * 2.0f, glowDx, glowDy)
                    .glowBlend(xp.glowBlend)
                    .glowCore(xp.glowCoreRatio)
                    .glowColor(glowColor)
                    .layer(3);
            if (xp.style == XpBarStyle.SEGMENTED) {
                fillStyle.segments(xp.segments, xp.segmentGap, (float) width);
            }

            ReGlassApi.create(context)
                    .dimensions(x, y, fillWidth, height)
                    .cornerRadius(Math.min(corner, fillWidth * 0.5f))
                    .style(fillStyle)
                    .render();
        }

        LiquidGlassUniforms.get().tryApplyBlur(context);
    }

    private static int hsvToRgb(float hueDeg, float sat, float val) {
        float h = ((hueDeg % 360f) + 360f) % 360f / 60f;
        int sector = (int) Math.floor(h) % 6;
        float f = h - (float) Math.floor(h);
        float p = val * (1f - sat);
        float q = val * (1f - sat * f);
        float t = val * (1f - sat * (1f - f));
        float r, g, b;
        switch (sector) {
            case 0 -> { r = val; g = t; b = p; }
            case 1 -> { r = q; g = val; b = p; }
            case 2 -> { r = p; g = val; b = t; }
            case 3 -> { r = p; g = q; b = val; }
            case 4 -> { r = t; g = p; b = val; }
            default -> { r = val; g = p; b = q; }
        }
        int ir = Math.max(0, Math.min(255, Math.round(r * 255f)));
        int ig = Math.max(0, Math.min(255, Math.round(g * 255f)));
        int ib = Math.max(0, Math.min(255, Math.round(b * 255f)));
        return (ir << 16) | (ig << 8) | ib;
    }
}
