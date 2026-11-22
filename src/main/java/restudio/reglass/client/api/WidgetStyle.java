package restudio.reglass.client.api;

import restudio.reglass.client.runtime.GlowBlendMode;
import restudio.reglass.client.runtime.ReGlassAnim;

public class WidgetStyle {

    private boolean hasTint;
    private int tintColor;
    private float tintAlpha;

    private boolean hasSmoothing;
    private float smoothingFactor;

    private boolean hasBlurRadius;
    private int blurRadius;

    private boolean hasLayer;
    private int layer;

    private boolean hasFadeKey;
    private long fadeKey;

    private boolean hasShadowGeometry;
    private float shadowExpand;
    private float shadowFactor;
    private float shadowOffsetX;
    private float shadowOffsetY;
    private boolean hasShadowColor;
    private int shadowColor;
    private float shadowColorAlpha;

    private boolean hasRefThickness;
    private float refThickness;
    private boolean hasRefFactor;
    private float refFactor;
    private boolean hasRefDispersion;
    private float refDispersion;
    private boolean hasRefFresnelRange;
    private float refFresnelRange;
    private boolean hasRefFresnelHardness;
    private float refFresnelHardness;
    private boolean hasRefFresnelFactor;
    private float refFresnelFactor;

    private boolean hasGlare;
    private float glareRange;
    private float glareHardness;
    private float glareConvergence;
    private float glareOppositeFactor;
    private float glareFactor;
    private float glareAngleRad;

    private float cornerExponent = 2.0f;
    private float tintedGlass = 0.0f;

    private float glowIntensity = 0.0f;
    private float glowSpread = 0.0f;
    private float glowOffsetX = 0.0f;
    private float glowOffsetY = 0.0f;
    private GlowBlendMode glowBlend = GlowBlendMode.LIGHT_TRANSMISSION;
    private float glowCoreRatio = 0.45f;
    private int glowColor = 0xFFFFFF;
    private GlowBlendMode shadowBlend = GlowBlendMode.NORMAL;
    private float layerDepthBlend = 0.0f;
    private int segmentCount = 0;
    private float segmentGap = 0.0f;
    private float segmentBarWidth = 0.0f;

    public static WidgetStyle create() { return new WidgetStyle(); }

    public WidgetStyle tint(int color, float alpha) { this.hasTint = true; this.tintColor = color; this.tintAlpha = alpha; return this; }
    public WidgetStyle smoothing(float factor) { this.hasSmoothing = true; this.smoothingFactor = factor; return this; }
    public WidgetStyle layer(int v) { this.hasLayer = true; this.layer = v; return this; }
    public WidgetStyle fadeKey(long key) { this.hasFadeKey = true; this.fadeKey = key; return this; }
    public WidgetStyle blurRadius(int radius) { this.hasBlurRadius = true; this.blurRadius = Math.max(1, radius); return this; }
    public WidgetStyle shadow(float expand, float factor, float offsetX, float offsetY) { this.hasShadowGeometry = true; this.shadowExpand = expand; this.shadowFactor = factor; this.shadowOffsetX = offsetX; this.shadowOffsetY = offsetY; return this; }
    public WidgetStyle shadowColor(int color, float alpha) { this.hasShadowColor = true; this.shadowColor = color; this.shadowColorAlpha = alpha; return this; }
    public WidgetStyle refractionThickness(float v) { this.hasRefThickness = true; this.refThickness = v; return this; }
    public WidgetStyle refractionFactor(float v) { this.hasRefFactor = true; this.refFactor = v; return this; }
    public WidgetStyle refractionDispersion(float v) { this.hasRefDispersion = true; this.refDispersion = v; return this; }
    public WidgetStyle fresnelRange(float v) { this.hasRefFresnelRange = true; this.refFresnelRange = v; return this; }
    public WidgetStyle fresnelHardness(float v) { this.hasRefFresnelHardness = true; this.refFresnelHardness = v; return this; }
    public WidgetStyle fresnelFactor(float v) { this.hasRefFresnelFactor = true; this.refFresnelFactor = v; return this; }
    public WidgetStyle glareRange(float v) { this.hasGlare = true; this.glareRange = v; return this; }
    public WidgetStyle glareHardness(float v) { this.hasGlare = true; this.glareHardness = v; return this; }
    public WidgetStyle glareConvergence(float v) { this.hasGlare = true; this.glareConvergence = v; return this; }
    public WidgetStyle glareOppositeFactor(float v) { this.hasGlare = true; this.glareOppositeFactor = v; return this; }
    public WidgetStyle glareFactor(float v) { this.hasGlare = true; this.glareFactor = v; return this; }
    public WidgetStyle glareAngleRad(float v) { this.hasGlare = true; this.glareAngleRad = v; return this; }
    public WidgetStyle cornerExponent(float v) { this.cornerExponent = Math.max(2.0f, v); return this; }
    public WidgetStyle tintedGlass(float strength) { this.tintedGlass = Math.max(0.0f, Math.min(1.0f, strength)); return this; }
    public WidgetStyle glow(float intensity, float spreadGuiPx, float offsetXGuiPx, float offsetYGuiPx) {
        this.glowIntensity = Math.max(0.0f, Math.min(3.0f, intensity));
        this.glowSpread = Math.max(0.0f, spreadGuiPx);
        this.glowOffsetX = offsetXGuiPx;
        this.glowOffsetY = offsetYGuiPx;
        return this;
    }
    public WidgetStyle glowBlend(GlowBlendMode mode) { this.glowBlend = mode != null ? mode : GlowBlendMode.LIGHT_TRANSMISSION; return this; }
    public WidgetStyle glowCore(float ratio) { this.glowCoreRatio = Math.max(0.0f, Math.min(1.0f, ratio)); return this; }
    public WidgetStyle glowColor(int rgb) { this.glowColor = rgb; return this; }
    public WidgetStyle shadowBlend(GlowBlendMode mode) { this.shadowBlend = mode != null ? mode : GlowBlendMode.NORMAL; return this; }
    public WidgetStyle layerDepthBlend(float k) { this.layerDepthBlend = Math.max(0.0f, Math.min(1.0f, k)); return this; }
    public WidgetStyle segments(int count, float gapGuiPx, float barWidthGuiPx) {
        this.segmentCount = count < 2 ? 0 : count;
        this.segmentGap = Math.max(0.0f, gapGuiPx);
        this.segmentBarWidth = Math.max(0.0f, barWidthGuiPx);
        return this;
    }
    @Deprecated
    public WidgetStyle coloredGlow(float strength) { return glow(strength, 6.0f, 0.0f, 1.0f); }

    public int getTintColor() { return hasTint ? tintColor : ReGlassConfig.INSTANCE.defaultTintColor; }
    public float getTintAlpha() { return hasTint ? tintAlpha : ReGlassAnim.INSTANCE.tintAlpha(); }

    public float getSmoothing() { return hasSmoothing ? smoothingFactor : ReGlassAnim.INSTANCE.smoothing(); }
    public int getLayer() { return hasLayer ? layer : ReGlassConfig.INSTANCE.defaultLayer; }
    public boolean hasFadeKey() { return hasFadeKey; }
    public long getFadeKey() { return fadeKey; }
    public int getBlurRadius() { return hasBlurRadius ? blurRadius : ReGlassAnim.INSTANCE.blurRadiusInt(); }

    public float getShadowExpand() { return hasShadowGeometry ? shadowExpand : ReGlassAnim.INSTANCE.shadowExpand(); }
    public float getShadowFactor() { return hasShadowGeometry ? shadowFactor : ReGlassAnim.INSTANCE.shadowFactor(); }
    public float getShadowOffsetX() { return hasShadowGeometry ? shadowOffsetX : ReGlassAnim.INSTANCE.shadowOffsetX(); }
    public float getShadowOffsetY() { return hasShadowGeometry ? shadowOffsetY : ReGlassAnim.INSTANCE.shadowOffsetY(); }
    public int getShadowColor() { return hasShadowColor ? shadowColor : ReGlassConfig.INSTANCE.defaultShadowColor; }
    public float getShadowColorAlpha() { return hasShadowColor ? shadowColorAlpha : ReGlassConfig.INSTANCE.defaultShadowColorAlpha; }

    public float getRefThickness() { return hasRefThickness ? refThickness : ReGlassAnim.INSTANCE.refThickness(); }
    public float getRefFactor() { return hasRefFactor ? refFactor : ReGlassAnim.INSTANCE.refFactor(); }
    public float getRefDispersion() { return hasRefDispersion ? refDispersion : ReGlassAnim.INSTANCE.refDispersion(); }
    public float getRefFresnelRange() { return hasRefFresnelRange ? refFresnelRange : ReGlassAnim.INSTANCE.refFresnelRange(); }
    public float getRefFresnelHardness() { return hasRefFresnelHardness ? refFresnelHardness : ReGlassAnim.INSTANCE.refFresnelHardness(); }
    public float getRefFresnelFactor() { return hasRefFresnelFactor ? refFresnelFactor : ReGlassAnim.INSTANCE.refFresnelFactor(); }

    public float getGlareRange() { return hasGlare ? glareRange : ReGlassAnim.INSTANCE.glareRange(); }
    public float getGlareHardness() { return hasGlare ? glareHardness : ReGlassAnim.INSTANCE.glareHardness(); }
    public float getGlareConvergence() { return hasGlare ? glareConvergence : ReGlassAnim.INSTANCE.glareConvergence(); }
    public float getGlareOppositeFactor() { return hasGlare ? glareOppositeFactor : ReGlassAnim.INSTANCE.glareOppositeFactor(); }
    public float getGlareFactor() { return hasGlare ? glareFactor : ReGlassAnim.INSTANCE.glareFactor(); }
    public float getGlareAngleRad() { return hasGlare ? glareAngleRad : ReGlassAnim.INSTANCE.glareAngleRad(); }
    public float getCornerExponent() { return cornerExponent; }
    public float getTintedGlass() { return tintedGlass; }
    public float getGlowIntensity() { return glowIntensity; }
    public float getGlowSpread() { return glowSpread; }
    public float getGlowOffsetX() { return glowOffsetX; }
    public float getGlowOffsetY() { return glowOffsetY; }
    public GlowBlendMode getGlowBlend() { return glowBlend; }
    public float getGlowCoreRatio() { return glowCoreRatio; }
    public int getGlowColor() { return glowColor; }
    public GlowBlendMode getShadowBlend() { return shadowBlend; }
    public float getLayerDepthBlend() { return layerDepthBlend; }
    public int getSegmentCount() { return segmentCount; }
    public float getSegmentGap() { return segmentGap; }
    public float getSegmentBarWidth() { return segmentBarWidth; }
}
