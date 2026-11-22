package restudio.reglass.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.runtime.GlowBlendMode;
import restudio.reglass.client.runtime.HoverCurve;
import restudio.reglass.client.runtime.SelectionShape;
import restudio.reglass.client.runtime.XpBarStyle;

public final class ReGlassSettingsIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("reglass.json");
    }

    public static void loadIntoMemory() {
        try {
            Path p = configPath();
            if (!Files.exists(p)) {
                saveFromMemory();
                return;
            }
            try (Reader r = Files.newBufferedReader(p)) {
                Data d = GSON.fromJson(r, Data.class);
                if (d == null) return;
                apply(d);
            }
        } catch (Exception ignored) {
        }
    }

    public static void saveFromMemory() {
        try {
            Path p = configPath();
            Data d = snapshot();
            try (Writer w = Files.newBufferedWriter(p)) {
                GSON.toJson(d, w);
            }
        } catch (Exception ignored) {
        }
    }

    private static Data snapshot() {
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        Data d = new Data();
        d.features_enableRedesign = c.features.enableRedesign;
        d.features_buttons = c.features.buttons;
        d.features_sliders = c.features.sliders;
        d.features_hotbar = c.features.hotbar;
        d.features_containers = c.features.containers;
        d.features_slotSelection = c.features.slotSelection;
        d.features_experienceBar = c.features.experienceBar;
        d.containerTextPanels = c.features.containerTextPanels;
        d.features_cancelScreenDarkening = c.features.cancelScreenDarkening;
        d.features_pixelatedGrid = c.features.pixelatedGrid;

        d.tintColor = c.defaultTintColor;
        d.tintAlpha = c.defaultTintAlpha;
        d.smoothing = c.defaultSmoothing;
        d.blurRadius = c.defaultBlurRadius;

        d.shadowExpand = c.defaultShadowExpand;
        d.shadowFactor = c.defaultShadowFactor;
        d.shadowOffsetX = c.defaultShadowOffsetX;
        d.shadowOffsetY = c.defaultShadowOffsetY;
        d.shadowColor = c.defaultShadowColor;
        d.shadowColorAlpha = c.defaultShadowColorAlpha;

        d.refThickness = c.defaultRefThickness;
        d.refFactor = c.defaultRefFactor;
        d.refDispersion = c.defaultRefDispersion;
        d.refFresnelRange = c.defaultRefFresnelRange;
        d.refFresnelHardness = c.defaultRefFresnelHardness;
        d.refFresnelFactor = c.defaultRefFresnelFactor;

        d.glareRange = c.defaultGlareRange;
        d.glareHardness = c.defaultGlareHardness;
        d.glareConvergence = c.defaultGlareConvergence;
        d.glareOppositeFactor = c.defaultGlareOppositeFactor;
        d.glareFactor = c.defaultGlareFactor;
        d.glareAngleRad = c.defaultGlareAngleRad;

        d.pixelatedGridSize = c.pixelatedGridSize;

        d.hoverScalePx = c.hoverScalePx;
        d.hoverInCurve = (c.hoverInCurve != null ? c.hoverInCurve : HoverCurve.CLASSIC).name();
        d.hoverOutCurve = (c.hoverOutCurve != null ? c.hoverOutCurve : HoverCurve.CLASSIC).name();
        d.hoverInDurationSec = c.hoverInDurationSec;
        d.hoverOutDurationSec = c.hoverOutDurationSec;
        d.hoverBounce = c.hoverBounce;
        d.focusScalePx = c.focusScalePx;
        d.focusBorderWidthPx = c.focusBorderWidthPx;
        d.focusBorderIntensity = c.focusBorderIntensity;
        d.focusBorderSpeed = c.focusBorderSpeed;
        d.slotSelectionShape = (c.slotSelectionShape != null ? c.slotSelectionShape : SelectionShape.SQUIRCLE).name();
        d.hotbarSelectionShape = (c.hotbarSelectionShape != null ? c.hotbarSelectionShape : SelectionShape.CIRCLE).name();
        d.xpBarGlowIntensity = c.xpBar.glowIntensity;
        d.xp_style = (c.xpBar.style != null ? c.xpBar.style : XpBarStyle.SMOOTH).name();
        d.xp_segments = c.xpBar.segments;
        d.xp_segmentGap = c.xpBar.segmentGap;
        d.xp_snapToSegments = c.xpBar.snapToSegments;
        d.xp_trackTintAlpha = c.xpBar.trackTintAlpha;
        d.xp_trackShadowIntensity = c.xpBar.trackShadowIntensity;
        d.xp_trackShadowSpread = c.xpBar.trackShadowSpread;
        d.xp_layerDepthBlend = c.xpBar.layerDepthBlend;
        d.xp_glowIntensity = c.xpBar.glowIntensity;
        d.xp_glowBlend = (c.xpBar.glowBlend != null ? c.xpBar.glowBlend : GlowBlendMode.LIGHT_TRANSMISSION).name();
        d.xp_glowSpread = c.xpBar.glowSpread;
        d.xp_glowAngleDeg = c.xpBar.glowAngleDeg;
        d.xp_glowDistance = c.xpBar.glowDistance;
        d.xp_glowCoreRatio = c.xpBar.glowCoreRatio;
        d.xp_glowColorCustom = c.xpBar.glowColorCustom;
        d.xp_glowHue = c.xpBar.glowHue;
        d.xp_fillShadowIntensity = c.xpBar.fillShadowIntensity;
        d.xp_fillShadowSpread = c.xpBar.fillShadowSpread;
        d.xp_fillShadowOffsetX = c.xpBar.fillShadowOffsetX;
        d.xp_fillShadowOffsetY = c.xpBar.fillShadowOffsetY;
        d.xp_fillShadowBlend = (c.xpBar.fillShadowBlend != null ? c.xpBar.fillShadowBlend : GlowBlendMode.MULTIPLY).name();
        d.xp_fillShadowUseTint = c.xpBar.fillShadowUseTint;

        return d;
    }

    private static float clampFloat(float value, float min, float max, float fallback) {
        return Float.isNaN(value) || Float.isInfinite(value) ? fallback : Math.max(min, Math.min(max, value));
    }

    public static void apply(Data d) {
        ReGlassConfig c = ReGlassConfig.INSTANCE;

        c.features.enableRedesign = d.features_enableRedesign;
        c.features.buttons = d.features_buttons;
        c.features.sliders = d.features_sliders;
        c.features.hotbar = d.features_hotbar;
        c.features.containers = d.features_containers;
        c.features.slotSelection = d.features_slotSelection;
        c.features.experienceBar = d.features_experienceBar;
        c.features.containerTextPanels = d.containerTextPanels;
        c.features.cancelScreenDarkening = d.features_cancelScreenDarkening;
        c.features.pixelatedGrid = d.features_pixelatedGrid;

        c.defaultTintColor = d.tintColor;
        c.defaultTintAlpha = d.tintAlpha;
        c.defaultSmoothing = d.smoothing;
        c.defaultBlurRadius = Math.max(1, d.blurRadius);

        c.defaultShadowExpand = d.shadowExpand;
        c.defaultShadowFactor = d.shadowFactor;
        c.defaultShadowOffsetX = d.shadowOffsetX;
        c.defaultShadowOffsetY = d.shadowOffsetY;
        c.defaultShadowColor = d.shadowColor;
        c.defaultShadowColorAlpha = d.shadowColorAlpha;

        c.defaultRefThickness = d.refThickness;
        c.defaultRefFactor = d.refFactor;
        c.defaultRefDispersion = d.refDispersion;
        c.defaultRefFresnelRange = d.refFresnelRange;
        c.defaultRefFresnelHardness = d.refFresnelHardness;
        c.defaultRefFresnelFactor = d.refFresnelFactor;

        c.defaultGlareRange = d.glareRange;
        c.defaultGlareHardness = d.glareHardness;
        c.defaultGlareConvergence = d.glareConvergence;
        c.defaultGlareOppositeFactor = d.glareOppositeFactor;
        c.defaultGlareFactor = d.glareFactor;
        c.defaultGlareAngleRad = d.glareAngleRad;

        c.pixelatedGridSize = d.pixelatedGridSize;

        c.hoverScalePx = Float.isNaN(d.hoverScalePx) ? 1.53f : Math.max(0f, Math.min(100f, d.hoverScalePx));
        c.hoverInCurve = HoverCurve.fromId(d.hoverInCurve, HoverCurve.CLASSIC);
        c.hoverOutCurve = HoverCurve.fromId(d.hoverOutCurve, HoverCurve.CLASSIC);
        c.hoverInDurationSec = Float.isNaN(d.hoverInDurationSec) ? 0.48f : Math.max(0f, Math.min(2f, d.hoverInDurationSec));
        c.hoverOutDurationSec = Float.isNaN(d.hoverOutDurationSec) ? 0.48f : Math.max(0f, Math.min(2f, d.hoverOutDurationSec));
        c.hoverBounce = Float.isNaN(d.hoverBounce) ? 0.25f : Math.max(0f, Math.min(0.75f, d.hoverBounce));
        c.focusScalePx = d.focusScalePx;
        c.focusBorderWidthPx = d.focusBorderWidthPx;
        c.focusBorderIntensity = d.focusBorderIntensity;
        c.focusBorderSpeed = d.focusBorderSpeed;
        c.slotSelectionShape = SelectionShape.fromId(d.slotSelectionShape, SelectionShape.SQUIRCLE);
        c.hotbarSelectionShape = SelectionShape.fromId(d.hotbarSelectionShape, SelectionShape.CIRCLE);

        float rawGlow = d.xp_glowIntensity != null ? d.xp_glowIntensity : d.xpBarGlowIntensity;
        c.xpBar.style = XpBarStyle.fromId(d.xp_style, XpBarStyle.SMOOTH);
        c.xpBar.segments = Math.max(2, Math.min(40, d.xp_segments));
        c.xpBar.segmentGap = clampFloat(d.xp_segmentGap, 0f, 3f, 1.0f);
        c.xpBar.snapToSegments = d.xp_snapToSegments;
        c.xpBar.trackTintAlpha = clampFloat(d.xp_trackTintAlpha, 0f, 0.6f, 0.12f);
        c.xpBar.trackShadowIntensity = clampFloat(d.xp_trackShadowIntensity, 0f, 1.5f, 0.35f);
        c.xpBar.trackShadowSpread = clampFloat(d.xp_trackShadowSpread, 0f, 12f, 3.0f);
        c.xpBar.layerDepthBlend = clampFloat(d.xp_layerDepthBlend, 0f, 1f, 0.7f);
        c.xpBar.glowIntensity = clampFloat(rawGlow, 0f, 2f, 1.0f);
        c.xpBar.glowBlend = GlowBlendMode.fromId(d.xp_glowBlend, GlowBlendMode.LIGHT_TRANSMISSION);
        c.xpBar.glowSpread = clampFloat(d.xp_glowSpread, 0f, 20f, 6.0f);
        c.xpBar.glowAngleDeg = clampFloat(d.xp_glowAngleDeg, 0f, 360f, 90.0f);
        c.xpBar.glowDistance = clampFloat(d.xp_glowDistance, 0f, 10f, 1.5f);
        c.xpBar.glowCoreRatio = clampFloat(d.xp_glowCoreRatio, 0f, 1f, 0.45f);
        c.xpBar.glowColorCustom = d.xp_glowColorCustom;
        c.xpBar.glowHue = clampFloat(d.xp_glowHue, 0f, 360f, 120.0f);
        c.xpBar.fillShadowIntensity = clampFloat(d.xp_fillShadowIntensity, 0f, 1.5f, 0.55f);
        c.xpBar.fillShadowSpread = clampFloat(d.xp_fillShadowSpread, 0f, 12f, 2.5f);
        c.xpBar.fillShadowOffsetX = clampFloat(d.xp_fillShadowOffsetX, -4f, 4f, 0.0f);
        c.xpBar.fillShadowOffsetY = clampFloat(d.xp_fillShadowOffsetY, -4f, 4f, 1.0f);
        c.xpBar.fillShadowBlend = GlowBlendMode.fromId(d.xp_fillShadowBlend, GlowBlendMode.MULTIPLY);
        c.xpBar.fillShadowUseTint = d.xp_fillShadowUseTint;
    }

    public static class Data {
        public boolean features_enableRedesign = true;
        public boolean features_buttons = true;
        public boolean features_sliders = true;
        public boolean features_hotbar = true;
        public boolean features_containers = true;
        public boolean features_slotSelection = true;
        public boolean features_experienceBar = true;
        @Deprecated
        public String containerStyle = "BATCH";
        public boolean containerTextPanels = true;
        public boolean features_cancelScreenDarkening = true;
        public boolean features_pixelatedGrid = false;

        public int tintColor = 0x000000;
        public float tintAlpha = 0f;
        public float smoothing = 0.003f;
        public int blurRadius = 8;

        public float shadowExpand = 18.2f;
        public float shadowFactor = 0.208f;
        public float shadowOffsetX = 0.0f;
        public float shadowOffsetY = 2.0f;
        public int shadowColor = 0x000000;
        public float shadowColorAlpha = 1.0f;

        public float refThickness = 13.15f;
        public float refFactor = 1.4f;
        public float refDispersion = 7.0f;
        public float refFresnelRange = 39.71f;
        public float refFresnelHardness = 20.0f;
        public float refFresnelFactor = 20.0f;

        public float glareRange = 30.0f;
        public float glareHardness = 20.0f;
        public float glareConvergence = 50.0f;
        public float glareOppositeFactor = 80.0f;
        public float glareFactor = 90.0f;
        public float glareAngleRad = -0.785f;

        public float pixelatedGridSize = 8.0f;

        public float hoverScalePx = 1.53f;
        public String hoverInCurve = "CLASSIC";
        public String hoverOutCurve = "CLASSIC";
        public float hoverInDurationSec = 0.48f;
        public float hoverOutDurationSec = 0.48f;
        public float hoverBounce = 0.25f;
        public float focusScalePx = 2.5f;
        public float focusBorderWidthPx = 2.04f;
        public float focusBorderIntensity = 0.75f;
        public float focusBorderSpeed = 1.6f;

        public String slotSelectionShape = "SQUIRCLE";
        public String hotbarSelectionShape = "CIRCLE";
        @Deprecated
        public float xpBarGlowIntensity = 1.0f;

        public String xp_style = "SMOOTH";
        public int xp_segments = 18;
        public float xp_segmentGap = 1.0f;
        public boolean xp_snapToSegments = false;
        public float xp_trackTintAlpha = 0.12f;
        public float xp_trackShadowIntensity = 0.35f;
        public float xp_trackShadowSpread = 3.0f;
        public float xp_layerDepthBlend = 0.7f;
        public Float xp_glowIntensity = null;
        public String xp_glowBlend = "LIGHT_TRANSMISSION";
        public float xp_glowSpread = 6.0f;
        public float xp_glowAngleDeg = 90.0f;
        public float xp_glowDistance = 1.5f;
        public float xp_glowCoreRatio = 0.45f;
        public boolean xp_glowColorCustom = false;
        public float xp_glowHue = 120.0f;
        public float xp_fillShadowIntensity = 0.55f;
        public float xp_fillShadowSpread = 2.5f;
        public float xp_fillShadowOffsetX = 0.0f;
        public float xp_fillShadowOffsetY = 1.0f;
        public String xp_fillShadowBlend = "MULTIPLY";
        public boolean xp_fillShadowUseTint = true;
    }
}
