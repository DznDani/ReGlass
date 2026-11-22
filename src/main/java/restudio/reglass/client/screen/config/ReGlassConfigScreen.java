package restudio.reglass.client.screen.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
//#if MC >= 26
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
//#else
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
//#endif
import restudio.reglass.client.LiquidGlassWidget;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.config.ReGlassSettingsIO;
import restudio.reglass.client.runtime.GlowBlendMode;
import restudio.reglass.client.runtime.HoverCurve;
import restudio.reglass.client.runtime.SelectionShape;
import restudio.reglass.client.runtime.XpBarGlassRenderer;
import restudio.reglass.client.runtime.XpBarStyle;
import restudio.reglass.client.ui.MappedSlider;

public class ReGlassConfigScreen extends Screen {
    private final Screen parent;
    private final List<PositionedWidget> positionedWidgets = new ArrayList<>();
    private LiquidGlassWidget previewCircle;
    private LiquidGlassWidget previewRounded;
    private int previewX;
    private int previewY;
    private double scrollPosition;
    private int totalListHeight;

//#if MC >= 26
    private record PositionedWidget(AbstractWidget widget, int y) {}
//#else
    private record PositionedWidget(ClickableWidget widget, int y) {}
//#endif

    public ReGlassConfigScreen(Screen parent) {
//#if MC >= 26
        super(Component.literal("ReGlass Configuration"));
//#else
        super(Text.literal("ReGlass Configuration"));
//#endif
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        positionedWidgets.clear();

        int listWidth = Math.min(300, this.width / 2 - 20);
        int widgetWidth = listWidth - 20;
        int widgetX = 10 + 10;

        int y = 5;
        int gap = 4;
        int widgetHeight = 20;

        ReGlassConfig cfg = ReGlassConfig.INSTANCE;

        addTitle("General", widgetX, y, widgetWidth);
        y += widgetHeight;

//#if MC >= 26
        Button enableRedesignButton = Button.builder(getEnableRedesignText(), button -> {
//#else
        ButtonWidget enableRedesignButton = ButtonWidget.builder(getEnableRedesignText(), button -> {
//#endif
            cfg.features.enableRedesign = !cfg.features.enableRedesign;
            button.setMessage(getEnableRedesignText());
//#if MC >= 26.2
            this.minecraft.gui.setScreen(new ReGlassConfigScreen(this.parent));
//#elseif MC >= 26
            this.minecraft.setScreen(new ReGlassConfigScreen(this.parent));
//#else
            this.client.setScreen(new ReGlassConfigScreen(this.parent));
//#endif
//#if MC >= 26
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
//#else
        }).dimensions(widgetX, y, widgetWidth, widgetHeight).build();
//#endif
        addPositionedWidget(enableRedesignButton, y);
        y += widgetHeight + gap;

//#if MC >= 26
        Button enableButtonsButton = Button.builder(getFeatureText("Buttons", cfg.features.buttons), button -> {
//#else
        ButtonWidget enableButtonsButton = ButtonWidget.builder(getFeatureText("Buttons", cfg.features.buttons), button -> {
//#endif
            cfg.features.buttons = !cfg.features.buttons;
            button.setMessage(getFeatureText("Buttons", cfg.features.buttons));
//#if MC >= 26
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
//#else
        }).dimensions(widgetX, y, widgetWidth, widgetHeight).build();
//#endif
        addPositionedWidget(enableButtonsButton, y).active = cfg.features.enableRedesign;
        y += widgetHeight + gap;

//#if MC >= 26
        Button enableSlidersButton = Button.builder(getFeatureText("Sliders", cfg.features.sliders), button -> {
//#else
        ButtonWidget enableSlidersButton = ButtonWidget.builder(getFeatureText("Sliders", cfg.features.sliders), button -> {
//#endif
            cfg.features.sliders = !cfg.features.sliders;
            button.setMessage(getFeatureText("Sliders", cfg.features.sliders));
//#if MC >= 26
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
//#else
        }).dimensions(widgetX, y, widgetWidth, widgetHeight).build();
//#endif
        addPositionedWidget(enableSlidersButton, y).active = cfg.features.enableRedesign;
        y += widgetHeight + gap;

//#if MC >= 26
        Button enableHotbarButton = Button.builder(getFeatureText("Hotbar", cfg.features.hotbar), button -> {
//#else
        ButtonWidget enableHotbarButton = ButtonWidget.builder(getFeatureText("Hotbar", cfg.features.hotbar), button -> {
//#endif
            cfg.features.hotbar = !cfg.features.hotbar;
            button.setMessage(getFeatureText("Hotbar", cfg.features.hotbar));
//#if MC >= 26
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
//#else
        }).dimensions(widgetX, y, widgetWidth, widgetHeight).build();
//#endif
        addPositionedWidget(enableHotbarButton, y).active = cfg.features.enableRedesign;
        y += widgetHeight + gap;

//#if MC >= 26
        Button enableContainersButton = Button.builder(getFeatureText("Containers", cfg.features.containers), button -> {
            cfg.features.containers = !cfg.features.containers;
            button.setMessage(getFeatureText("Containers", cfg.features.containers));
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
        addPositionedWidget(enableContainersButton, y).active = cfg.features.enableRedesign;
        y += widgetHeight + gap;

        Button containerTextPanelsButton = Button.builder(getFeatureText("Container Text Panels", cfg.features.containerTextPanels), button -> {
            cfg.features.containerTextPanels = !cfg.features.containerTextPanels;
            button.setMessage(getFeatureText("Container Text Panels", cfg.features.containerTextPanels));
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
        addPositionedWidget(containerTextPanelsButton, y).active = cfg.features.enableRedesign && cfg.features.containers;
        y += widgetHeight + gap;

        Button cancelDarkeningButton = Button.builder(getFeatureText("Cancel Screen Darkening", cfg.features.cancelScreenDarkening), button -> {
//#else
        ButtonWidget cancelDarkeningButton = ButtonWidget.builder(getFeatureText("Cancel Screen Darkening", cfg.features.cancelScreenDarkening), button -> {
//#endif
            cfg.features.cancelScreenDarkening = !cfg.features.cancelScreenDarkening;
            button.setMessage(getFeatureText("Cancel Screen Darkening", cfg.features.cancelScreenDarkening));
//#if MC >= 26
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
//#else
        }).dimensions(widgetX, y, widgetWidth, widgetHeight).build();
//#endif
        addPositionedWidget(cancelDarkeningButton, y).active = cfg.features.enableRedesign;
        y += widgetHeight + gap;

//#if MC >= 26
        Button slotSelectionButton = Button.builder(getFeatureText("Slot Selection", cfg.features.slotSelection), button -> {
            cfg.features.slotSelection = !cfg.features.slotSelection;
            button.setMessage(getFeatureText("Slot Selection", cfg.features.slotSelection));
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
//#else
        ButtonWidget slotSelectionButton = ButtonWidget.builder(getFeatureText("Slot Selection", cfg.features.slotSelection), button -> {
            cfg.features.slotSelection = !cfg.features.slotSelection;
            button.setMessage(getFeatureText("Slot Selection", cfg.features.slotSelection));
        }).dimensions(widgetX, y, widgetWidth, widgetHeight).build();
//#endif
        addPositionedWidget(slotSelectionButton, y).active = cfg.features.enableRedesign && cfg.features.containers;
        y += widgetHeight + gap;

        addShapeSelectorRow("Slot Shape", () -> cfg.slotSelectionShape, s -> cfg.slotSelectionShape = s, widgetX, y, widgetWidth, widgetHeight, cfg.features.enableRedesign && cfg.features.containers);
        y += widgetHeight + gap;

        addShapeSelectorRow("Hotbar Shape", () -> cfg.hotbarSelectionShape, s -> cfg.hotbarSelectionShape = s, widgetX, y, widgetWidth, widgetHeight, cfg.features.enableRedesign && cfg.features.hotbar);
        y += widgetHeight + gap * 2;

        addTitle("Experience Bar", widgetX, y, widgetWidth);
        y += widgetHeight;

        ReGlassConfig.XpBar xp = cfg.xpBar;
//#if MC >= 26
        List<AbstractWidget> xpAlwaysActive = new ArrayList<>();
        List<AbstractWidget> xpSegmentedActive = new ArrayList<>();
        List<AbstractWidget> xpCustomHueActive = new ArrayList<>();
//#else
        List<ClickableWidget> xpAlwaysActive = new ArrayList<>();
        List<ClickableWidget> xpSegmentedActive = new ArrayList<>();
        List<ClickableWidget> xpCustomHueActive = new ArrayList<>();
//#endif

        Runnable refreshXpActive = () -> {
            boolean base = cfg.features.enableRedesign && cfg.features.experienceBar;
            boolean segmented = base && xp.style == XpBarStyle.SEGMENTED;
            boolean customHue = base && xp.glowColorCustom;
            for (var w : xpAlwaysActive) w.active = base;
            for (var w : xpSegmentedActive) w.active = segmented;
            for (var w : xpCustomHueActive) w.active = customHue;
        };

        addLabelToggleRow(() -> "Experience Bar: " + (cfg.features.experienceBar ? "ON" : "OFF"),
                () -> { cfg.features.experienceBar = !cfg.features.experienceBar; refreshXpActive.run(); },
                widgetX, y, widgetWidth, widgetHeight, cfg.features.enableRedesign);
        y += widgetHeight + gap;

        xpAlwaysActive.addAll(addEnumSelectorRow("Bar Style", () -> xp.style, s -> xp.style = s,
                XpBarStyle::next, XpBarStyle::previous, XpBarStyle::displayName, refreshXpActive,
                widgetX, y, widgetWidth, widgetHeight, true));
        y += widgetHeight + gap;

        xpSegmentedActive.add(addIntSliderRow("Segments", 2, 40, xp.segments, v -> xp.segments = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpSegmentedActive.add(addFloatSliderRow("Segment Gap", 0f, 3f, xp.segmentGap, v -> xp.segmentGap = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpSegmentedActive.add(addLabelToggleRow(() -> "Snap to Segments: " + (xp.snapToSegments ? "ON" : "OFF"),
                () -> xp.snapToSegments = !xp.snapToSegments,
                widgetX, y, widgetWidth, widgetHeight, true));
        y += widgetHeight + gap;

        xpAlwaysActive.add(addFloatSliderRow("Track Opacity", 0f, 0.6f, xp.trackTintAlpha, v -> xp.trackTintAlpha = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addFloatSliderRow("Layer Depth Blend", 0f, 1f, xp.layerDepthBlend, v -> xp.layerDepthBlend = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;

        xpAlwaysActive.add(addFloatSliderRow("Glow Intensity", 0f, 2f, xp.glowIntensity, v -> xp.glowIntensity = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.addAll(addEnumSelectorRow("Glow Blend", () -> xp.glowBlend, b -> xp.glowBlend = b,
                GlowBlendMode::next, GlowBlendMode::previous, GlowBlendMode::displayName, null,
                widgetX, y, widgetWidth, widgetHeight, true));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addFloatSliderRow("Glow Spread", 0f, 20f, xp.glowSpread, v -> xp.glowSpread = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addIntSliderRow("Glow Direction (deg)", 0, 360, Math.round(xp.glowAngleDeg), v -> xp.glowAngleDeg = v.floatValue(), widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addFloatSliderRow("Glow Distance", 0f, 10f, xp.glowDistance, v -> xp.glowDistance = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addFloatSliderRow("Glow Core Ratio", 0f, 1f, xp.glowCoreRatio, v -> xp.glowCoreRatio = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addLabelToggleRow(() -> "Glow Color: " + (xp.glowColorCustom ? "Custom" : "Match Tint"),
                () -> { xp.glowColorCustom = !xp.glowColorCustom; refreshXpActive.run(); },
                widgetX, y, widgetWidth, widgetHeight, true));
        y += widgetHeight + gap;
        xpCustomHueActive.add(addIntSliderRow("Glow Hue (deg)", 0, 360, Math.round(xp.glowHue), v -> xp.glowHue = v.floatValue(), widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;

        xpAlwaysActive.add(addFloatSliderRow("Fill Shadow Intensity", 0f, 1.5f, xp.fillShadowIntensity, v -> xp.fillShadowIntensity = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addFloatSliderRow("Fill Shadow Spread", 0f, 12f, xp.fillShadowSpread, v -> xp.fillShadowSpread = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addFloatSliderRow("Fill Shadow Offset X", -4f, 4f, xp.fillShadowOffsetX, v -> xp.fillShadowOffsetX = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addFloatSliderRow("Fill Shadow Offset Y", -4f, 4f, xp.fillShadowOffsetY, v -> xp.fillShadowOffsetY = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.addAll(addEnumSelectorRow("Fill Shadow Blend", () -> xp.fillShadowBlend, b -> xp.fillShadowBlend = b,
                GlowBlendMode::next, GlowBlendMode::previous, GlowBlendMode::displayName, null,
                widgetX, y, widgetWidth, widgetHeight, true));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addLabelToggleRow(() -> "Fill Shadow Color: " + (xp.fillShadowUseTint ? "Tinted" : "Black"),
                () -> xp.fillShadowUseTint = !xp.fillShadowUseTint,
                widgetX, y, widgetWidth, widgetHeight, true));
        y += widgetHeight + gap;

        xpAlwaysActive.add(addFloatSliderRow("Track Shadow Intensity", 0f, 1.5f, xp.trackShadowIntensity, v -> xp.trackShadowIntensity = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap;
        xpAlwaysActive.add(addFloatSliderRow("Track Shadow Spread", 0f, 12f, xp.trackShadowSpread, v -> xp.trackShadowSpread = v, widgetX, y, widgetWidth, widgetHeight));
        y += widgetHeight + gap * 2;
        refreshXpActive.run();

        addTitle("Appearance", widgetX, y, widgetWidth);
        y += widgetHeight;

//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Tint Alpha"), 0f, 1f, cfg.defaultTintAlpha, v -> cfg.defaultTintAlpha = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Tint Alpha"), 0f, 1f, cfg.defaultTintAlpha, v -> cfg.defaultTintAlpha = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.intSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Blur Radius"), 0, 32, cfg.defaultBlurRadius, v -> cfg.defaultBlurRadius = v), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.intSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Blur Radius"), 0, 32, cfg.defaultBlurRadius, v -> cfg.defaultBlurRadius = v), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Smoothing"), -0.02f, 0.02f, cfg.defaultSmoothing, v -> cfg.defaultSmoothing = v.floatValue()), y).active = cfg.features.enableRedesign;
        y += widgetHeight + gap;
        addSlider(MappedSlider.intSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Default Layer"), 0, 15, cfg.defaultLayer, v -> cfg.defaultLayer = v), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Smoothing"), -0.02f, 0.02f, cfg.defaultSmoothing, v -> cfg.defaultSmoothing = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap * 2;

        addTitle("Shadow", widgetX, y, widgetWidth);
        y += widgetHeight;

//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Shadow Expand"), 0f, 100f, cfg.defaultShadowExpand, v -> cfg.defaultShadowExpand = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Shadow Expand"), 0f, 100f, cfg.defaultShadowExpand, v -> cfg.defaultShadowExpand = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Shadow Factor"), 0f, 1f, cfg.defaultShadowFactor, v -> cfg.defaultShadowFactor = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Shadow Factor"), 0f, 1f, cfg.defaultShadowFactor, v -> cfg.defaultShadowFactor = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Shadow Offset Y"), -10f, 10f, cfg.defaultShadowOffsetY, v -> cfg.defaultShadowOffsetY = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Shadow Offset Y"), -10f, 10f, cfg.defaultShadowOffsetY, v -> cfg.defaultShadowOffsetY = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap * 2;

        addTitle("Refraction", widgetX, y, widgetWidth);
        y += widgetHeight;

//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Refraction Thickness"), 1f, 60f, cfg.defaultRefThickness, v -> cfg.defaultRefThickness = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Refraction Thickness"), 1f, 60f, cfg.defaultRefThickness, v -> cfg.defaultRefThickness = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Refraction Factor"), 1.0f, 2.5f, cfg.defaultRefFactor, v -> cfg.defaultRefFactor = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Refraction Factor"), 1.0f, 2.5f, cfg.defaultRefFactor, v -> cfg.defaultRefFactor = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Dispersion"), 0f, 20f, cfg.defaultRefDispersion, v -> cfg.defaultRefDispersion = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Dispersion"), 0f, 20f, cfg.defaultRefDispersion, v -> cfg.defaultRefDispersion = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Fresnel Range"), 0f, 60f, cfg.defaultRefFresnelRange, v -> cfg.defaultRefFresnelRange = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Fresnel Range"), 0f, 60f, cfg.defaultRefFresnelRange, v -> cfg.defaultRefFresnelRange = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Fresnel Hardness"), 0f, 100f, cfg.defaultRefFresnelHardness, v -> cfg.defaultRefFresnelHardness = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Fresnel Hardness"), 0f, 100f, cfg.defaultRefFresnelHardness, v -> cfg.defaultRefFresnelHardness = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Fresnel Factor"), 0f, 100f, cfg.defaultRefFresnelFactor, v -> cfg.defaultRefFresnelFactor = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Fresnel Factor"), 0f, 100f, cfg.defaultRefFresnelFactor, v -> cfg.defaultRefFresnelFactor = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap * 2;

        addTitle("Glare", widgetX, y, widgetWidth);
        y += widgetHeight;

//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Glare Range"), 0f, 60f, cfg.defaultGlareRange, v -> cfg.defaultGlareRange = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Glare Range"), 0f, 60f, cfg.defaultGlareRange, v -> cfg.defaultGlareRange = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Glare Factor"), 0f, 100f, cfg.defaultGlareFactor, v -> cfg.defaultGlareFactor = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Glare Factor"), 0f, 100f, cfg.defaultGlareFactor, v -> cfg.defaultGlareFactor = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap * 2;

        addTitle("Interactions", widgetX, y, widgetWidth);
        y += widgetHeight;

//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Hover Scale (px)"), 0f, 30f, cfg.hoverScalePx, v -> cfg.hoverScalePx = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Hover Scale (px)"), 0f, 30f, cfg.hoverScalePx, v -> cfg.hoverScalePx = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
        addCurveSelectorRow("Hover In", () -> cfg.hoverInCurve, c -> cfg.hoverInCurve = c, widgetX, y, widgetWidth, widgetHeight, cfg.features.enableRedesign);
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Hover In Duration (s)"), 0f, 2f, cfg.hoverInDurationSec, v -> cfg.hoverInDurationSec = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Hover In Duration (s)"), 0f, 2f, cfg.hoverInDurationSec, v -> cfg.hoverInDurationSec = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
        addCurveSelectorRow("Hover Out", () -> cfg.hoverOutCurve, c -> cfg.hoverOutCurve = c, widgetX, y, widgetWidth, widgetHeight, cfg.features.enableRedesign);
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Hover Out Duration (s)"), 0f, 2f, cfg.hoverOutDurationSec, v -> cfg.hoverOutDurationSec = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Hover Out Duration (s)"), 0f, 2f, cfg.hoverOutDurationSec, v -> cfg.hoverOutDurationSec = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Hover Bounce (iOS)"), 0f, 0.75f, cfg.hoverBounce, v -> cfg.hoverBounce = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Hover Bounce (iOS)"), 0f, 0.75f, cfg.hoverBounce, v -> cfg.hoverBounce = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Press Flex (px)"), 0f, 8f, cfg.focusScalePx, v -> cfg.focusScalePx = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Press Flex (px)"), 0f, 8f, cfg.focusScalePx, v -> cfg.focusScalePx = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Press Rim Width (px)"), 0f, 6f, cfg.focusBorderWidthPx, v -> cfg.focusBorderWidthPx = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Press Rim Width (px)"), 0f, 6f, cfg.focusBorderWidthPx, v -> cfg.focusBorderWidthPx = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Press Light Strength"), 0f, 1f, cfg.focusBorderIntensity, v -> cfg.focusBorderIntensity = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Press Light Strength"), 0f, 1f, cfg.focusBorderIntensity, v -> cfg.focusBorderIntensity = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Press Response Speed"), 0f, 4f, cfg.focusBorderSpeed, v -> cfg.focusBorderSpeed = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Press Response Speed"), 0f, 4f, cfg.focusBorderSpeed, v -> cfg.focusBorderSpeed = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap * 2;

        addTitle("Debug", widgetX, y, widgetWidth);
        y += widgetHeight;

//#if MC >= 26
        Button pixelatedGridButton = Button.builder(getFeatureText("Pixelated Grid", cfg.features.pixelatedGrid), button -> {
//#else
        ButtonWidget pixelatedGridButton = ButtonWidget.builder(getFeatureText("Pixelated Grid", cfg.features.pixelatedGrid), button -> {
//#endif
            cfg.features.pixelatedGrid = !cfg.features.pixelatedGrid;
            button.setMessage(getFeatureText("Pixelated Grid", cfg.features.pixelatedGrid));
//#if MC >= 26
        }).bounds(widgetX, y, widgetWidth, widgetHeight).build();
//#else
        }).dimensions(widgetX, y, widgetWidth, widgetHeight).build();
//#endif
        addPositionedWidget(pixelatedGridButton, y).active = cfg.features.enableRedesign;
        y += widgetHeight + gap;

//#if MC >= 26
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Grid Size"), 1f, 32f, cfg.pixelatedGridSize, v -> cfg.pixelatedGridSize = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.floatSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Grid Size"), 1f, 32f, cfg.pixelatedGridSize, v -> cfg.pixelatedGridSize = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;
//#if MC >= 26
        addSlider(MappedSlider.intSlider(widgetX, y, widgetWidth, widgetHeight, Component.literal("Debug Step"), 0, 9, Math.round(cfg.debugStep), v -> cfg.debugStep = v.floatValue()), y).active = cfg.features.enableRedesign;
//#else
        addSlider(MappedSlider.intSlider(widgetX, y, widgetWidth, widgetHeight, Text.literal("Debug Step"), 0, 9, Math.round(cfg.debugStep), v -> cfg.debugStep = v.floatValue()), y).active = cfg.features.enableRedesign;
//#endif
        y += widgetHeight + gap;

        this.totalListHeight = y;

//#if MC >= 26
        addRenderableWidget(Button.builder(Component.translatable("controls.reset"), b -> {
//#else
        addDrawableChild(ButtonWidget.builder(Text.translatable("controls.reset"), b -> {
//#endif
            ReGlassSettingsIO.apply(new ReGlassSettingsIO.Data());
//#if MC >= 26.2
            if (this.minecraft != null) {
                this.minecraft.gui.setScreen(new ReGlassConfigScreen(this.parent));
//#elseif MC >= 26
            if (this.minecraft != null) {
                this.minecraft.setScreen(new ReGlassConfigScreen(this.parent));
//#else
            if (this.client != null) {
                this.client.setScreen(new ReGlassConfigScreen(this.parent));
//#endif
            }
//#if MC >= 26
        }).bounds(this.width / 2 - 100, this.height - 28, 98, 20).build());
//#else
        }).dimensions(this.width / 2 - 100, this.height - 28, 98, 20).build());
//#endif

//#if MC >= 26
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose()).bounds(this.width / 2 + 2, this.height - 28, 98, 20).build());
//#else
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), b -> close()).dimensions(this.width / 2 + 2, this.height - 28, 98, 20).build());
//#endif

        this.previewX = this.width / 2 + 20;
        this.previewY = this.height / 2 - 50;

        WidgetStyle s1 = WidgetStyle.create().tint(0xFFFFFF, Math.min(1f, Math.max(0f, cfg.defaultTintAlpha))).blurRadius(cfg.defaultBlurRadius)
                .shadow(cfg.defaultShadowExpand, cfg.defaultShadowFactor, cfg.defaultShadowOffsetX, cfg.defaultShadowOffsetY)
                .shadowColor(cfg.defaultShadowColor, cfg.defaultShadowColorAlpha)
                .refractionThickness(cfg.defaultRefThickness).refractionFactor(cfg.defaultRefFactor).refractionDispersion(cfg.defaultRefDispersion)
                .fresnelRange(cfg.defaultRefFresnelRange).fresnelHardness(cfg.defaultRefFresnelHardness).fresnelFactor(cfg.defaultRefFresnelFactor)
                .glareRange(cfg.defaultGlareRange).glareHardness(cfg.defaultGlareHardness).glareConvergence(cfg.defaultGlareConvergence)
//#if MC >= 26
                .glareOppositeFactor(cfg.defaultGlareOppositeFactor).glareFactor(cfg.defaultGlareFactor).glareAngleRad(cfg.defaultGlareAngleRad)
                .cornerExponent(cfg.slotSelectionShape.cornerExponent())
                .layer(1);
//#else
                .glareOppositeFactor(cfg.defaultGlareOppositeFactor).glareFactor(cfg.defaultGlareFactor).glareAngleRad(cfg.defaultGlareAngleRad)
                .cornerExponent(cfg.slotSelectionShape.cornerExponent());
//#endif

//#if MC >= 26
        previewCircle = addRenderableWidget(new LiquidGlassWidget(this.previewX + 40, this.previewY, 100, 100, s1).setCornerRadiusPx(50f));
//#else
        previewCircle = addDrawableChild(new LiquidGlassWidget(this.previewX, this.previewY, 100, 100, s1).setCornerRadiusPx(50f));
//#endif

        WidgetStyle s2 = WidgetStyle.create().tint(cfg.defaultTintColor, cfg.defaultTintAlpha).blurRadius(cfg.defaultBlurRadius)
                .shadow(cfg.defaultShadowExpand, cfg.defaultShadowFactor, cfg.defaultShadowOffsetX, cfg.defaultShadowOffsetY)
//#if MC >= 26
                .shadowColor(cfg.defaultShadowColor, cfg.defaultShadowColorAlpha)
                .layer(0);
//#else
                .shadowColor(cfg.defaultShadowColor, cfg.defaultShadowColorAlpha);
//#endif

//#if MC >= 26
        previewRounded = addRenderableWidget(new LiquidGlassWidget(this.previewX + 20, this.previewY + 120, 140, 60, s2).setCornerRadiusPx(16f));
//#else
        previewRounded = addDrawableChild(new LiquidGlassWidget(this.previewX + 110, this.previewY + 20, 140, 60, s2).setCornerRadiusPx(16f));
//#endif
    }

//#if MC >= 26
    private Component getEnableRedesignText() {
        return Component.literal("ReGlass Redesign: ").append(ReGlassConfig.INSTANCE.features.enableRedesign ? Component.translatable("options.on") : Component.translatable("options.off"));
//#else
    private Text getEnableRedesignText() {
        return Text.literal("ReGlass Redesign: ").append(ReGlassConfig.INSTANCE.features.enableRedesign ? Text.translatable("options.on") : Text.translatable("options.off"));
//#endif
    }

//#if MC >= 26
    private Component getFeatureText(String feature, boolean enabled) {
        return Component.literal(feature + ": ").append(enabled ? Component.translatable("options.on") : Component.translatable("options.off"));
//#else
    private Text getFeatureText(String feature, boolean enabled) {
        return Text.literal(feature + ": ").append(enabled ? Text.translatable("options.on") : Text.translatable("options.off"));
//#endif
    }

//#if MC >= 26
    private <T extends AbstractWidget> T addPositionedWidget(T widget, int y) {
//#else
    private <T extends ClickableWidget> T addPositionedWidget(T widget, int y) {
//#endif
        positionedWidgets.add(new PositionedWidget(widget, y));
//#if MC >= 26
        return addRenderableWidget(widget);
//#else
        return addDrawableChild(widget);
//#endif
    }

    private MappedSlider addSlider(MappedSlider slider, int y) {
        return addPositionedWidget(slider, y);
    }

    private void addTitle(String title, int x, int y, int width) {
//#if MC >= 26
        addPositionedWidget(new TitleWidget(x, y, width, 20, Component.literal(title)), y);
//#else
        addPositionedWidget(new TitleWidget(x, y, width, 20, Text.literal(title)), y);
//#endif
    }

    private void addCurveSelectorRow(
            String label,
            Supplier<HoverCurve> getter,
            Consumer<HoverCurve> setter,
            int x,
            int y,
            int width,
            int height,
            boolean active
    ) {
        addEnumSelectorRow(label, getter, setter, HoverCurve::next, HoverCurve::previous, HoverCurve::displayName, null, x, y, width, height, active);
    }

    private void addShapeSelectorRow(
            String label,
            Supplier<SelectionShape> getter,
            Consumer<SelectionShape> setter,
            int x,
            int y,
            int width,
            int height,
            boolean active
    ) {
        addEnumSelectorRow(label, getter, setter, SelectionShape::next, SelectionShape::previous, SelectionShape::displayName, null, x, y, width, height, active);
    }

//#if MC >= 26
    private <T> List<AbstractWidget> addEnumSelectorRow(
//#else
    private <T> List<ClickableWidget> addEnumSelectorRow(
//#endif
            String label,
            Supplier<T> getter,
            Consumer<T> setter,
            UnaryOperator<T> nextFn,
            UnaryOperator<T> prevFn,
            Function<T, String> nameFn,
            Runnable onChange,
            int x,
            int y,
            int width,
            int height,
            boolean active
    ) {
        int arrowWidth = 20;
        int centerWidth = Math.max(40, width - arrowWidth * 2 - 4);
        int centerX = x + arrowWidth + 2;
        int rightX = x + width - arrowWidth;

//#if MC >= 26
        Button centerBtn = Button.builder(Component.literal(label + ": " + nameFn.apply(getter.get())), button -> {
            setter.accept(nextFn.apply(getter.get()));
            button.setMessage(Component.literal(label + ": " + nameFn.apply(getter.get())));
            if (onChange != null) onChange.run();
        }).bounds(centerX, y, centerWidth, height).build();
        Button prevBtn = Button.builder(Component.literal("<"), button -> {
            setter.accept(prevFn.apply(getter.get()));
            centerBtn.setMessage(Component.literal(label + ": " + nameFn.apply(getter.get())));
            if (onChange != null) onChange.run();
        }).bounds(x, y, arrowWidth, height).build();
        Button nextBtn = Button.builder(Component.literal(">"), button -> {
            setter.accept(nextFn.apply(getter.get()));
            centerBtn.setMessage(Component.literal(label + ": " + nameFn.apply(getter.get())));
            if (onChange != null) onChange.run();
        }).bounds(rightX, y, arrowWidth, height).build();
//#else
        ButtonWidget centerBtn = ButtonWidget.builder(Text.literal(label + ": " + nameFn.apply(getter.get())), button -> {
            setter.accept(nextFn.apply(getter.get()));
            button.setMessage(Text.literal(label + ": " + nameFn.apply(getter.get())));
            if (onChange != null) onChange.run();
        }).dimensions(centerX, y, centerWidth, height).build();
        ButtonWidget prevBtn = ButtonWidget.builder(Text.literal("<"), button -> {
            setter.accept(prevFn.apply(getter.get()));
            centerBtn.setMessage(Text.literal(label + ": " + nameFn.apply(getter.get())));
            if (onChange != null) onChange.run();
        }).dimensions(x, y, arrowWidth, height).build();
        ButtonWidget nextBtn = ButtonWidget.builder(Text.literal(">"), button -> {
            setter.accept(nextFn.apply(getter.get()));
            centerBtn.setMessage(Text.literal(label + ": " + nameFn.apply(getter.get())));
            if (onChange != null) onChange.run();
        }).dimensions(rightX, y, arrowWidth, height).build();
//#endif

        addPositionedWidget(prevBtn, y).active = active;
        addPositionedWidget(centerBtn, y).active = active;
        addPositionedWidget(nextBtn, y).active = active;
        return List.of(prevBtn, centerBtn, nextBtn);
    }

//#if MC >= 26
    private Button addLabelToggleRow(
//#else
    private ButtonWidget addLabelToggleRow(
//#endif
            Supplier<String> labelSupplier,
            Runnable onToggle,
            int x,
            int y,
            int width,
            int height,
            boolean active
    ) {
//#if MC >= 26
        Button btn = Button.builder(Component.literal(labelSupplier.get()), button -> {
            onToggle.run();
            button.setMessage(Component.literal(labelSupplier.get()));
        }).bounds(x, y, width, height).build();
//#else
        ButtonWidget btn = ButtonWidget.builder(Text.literal(labelSupplier.get()), button -> {
            onToggle.run();
            button.setMessage(Text.literal(labelSupplier.get()));
        }).dimensions(x, y, width, height).build();
//#endif
        addPositionedWidget(btn, y).active = active;
        return btn;
    }

    private MappedSlider addFloatSliderRow(String label, float min, float max, float init, Consumer<Float> setter, int x, int y, int width, int height) {
//#if MC >= 26
        return addSlider(MappedSlider.floatSlider(x, y, width, height, Component.literal(label), min, max, init, v -> setter.accept(v.floatValue())), y);
//#else
        return addSlider(MappedSlider.floatSlider(x, y, width, height, Text.literal(label), min, max, init, v -> setter.accept(v.floatValue())), y);
//#endif
    }

    private MappedSlider addIntSliderRow(String label, int min, int max, int init, Consumer<Integer> setter, int x, int y, int width, int height) {
//#if MC >= 26
        return addSlider(MappedSlider.intSlider(x, y, width, height, Component.literal(label), min, max, init, setter), y);
//#else
        return addSlider(MappedSlider.intSlider(x, y, width, height, Text.literal(label), min, max, init, setter), y);
//#endif
    }

    @Override
//#if MC >= 26
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
//#else
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

//#endif
        ReGlassConfig cfg = ReGlassConfig.INSTANCE;
        if (this.previewCircle != null) {
            this.previewCircle.style.cornerExponent(cfg.slotSelectionShape.cornerExponent());
        }

        int listTop = 32;
        int listBottom = this.height - 32;

        for (PositionedWidget pw : positionedWidgets) {
            pw.widget.setY(pw.y() + listTop - (int) this.scrollPosition);
            if (pw.widget() instanceof TitleWidget tw) {
                tw.visible = (pw.widget.getY() >= listTop && (pw.widget.getY() + 20) <= listBottom);
            } else {
                pw.widget.visible = (pw.widget.getY() >= listTop && (pw.widget.getY() + pw.widget.getHeight()) <= listBottom);
            }
        }

//#if MC >= 26
        super.extractRenderState(context, mouseX, mouseY, delta);
//#else
        super.render(context, mouseX, mouseY, delta);
//#endif

        if (cfg.features.enableRedesign) {
            int xpWidth = Math.min(182, Math.max(80, this.width - this.previewX - 20));
            int xpX = this.previewX;
            int xpY = Math.max(38, this.previewY - 22);
            double phase = (System.currentTimeMillis() % 6000L) / 6000.0;
            double previewProgress = 0.15 + 0.80 * (0.5 - 0.5 * Math.cos(phase * Math.PI * 2.0));
            XpBarGlassRenderer.render(context, xpX, xpY, xpWidth, previewProgress, 0.0);
        }

//#if MC >= 26
        context.centeredText(this.font, Component.literal("ReGlass Config (Scrollable)"), this.width / 2, 15, 0xFFFFFFFF);
//#else
        context.drawCenteredTextWithShadow(this.textRenderer, Text.of("ReGlass Config (Scrollable)"), this.width / 2, 15, 0xFFFFFFFF);
//#endif
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int listHeight = (this.height - 32) - 32;
        int maxScroll = Math.max(0, this.totalListHeight - listHeight);
        if (maxScroll > 0) {
            this.scrollPosition -= verticalAmount * 10;
//#if MC >= 26
            this.scrollPosition = Mth.clamp(this.scrollPosition, 0, maxScroll);
//#else
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0, maxScroll);
//#endif
            return true;
        }
        return false;
    }

    @Override
//#if MC >= 26
    public void onClose() {
//#else
    public void close() {
//#endif
        ReGlassSettingsIO.saveFromMemory();
//#if MC >= 26.2
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(this.parent);
//#elseif MC >= 26
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
//#else
        if (this.client != null) {
            this.client.setScreen(this.parent);
//#endif
        }
    }

//#if MC >= 26
    private class TitleWidget extends AbstractWidget {
        public TitleWidget(int x, int y, int width, int height, Component message) {
//#else
    private class TitleWidget extends ClickableWidget {
        public TitleWidget(int x, int y, int width, int height, Text message) {
//#endif
            super(x, y, width, height, message);
        }

        @Override
//#if MC >= 26
        public void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
//#else
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
//#endif
            if (this.visible) {
//#if MC >= 26
                context.centeredText(ReGlassConfigScreen.this.font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, 0xFFFFFF);
//#else
                context.drawCenteredTextWithShadow(ReGlassConfigScreen.this.textRenderer, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, 0xFFFFFF);
//#endif
            }
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return false;
        }

        @Override
//#if MC >= 26
        protected void updateWidgetNarration(NarrationElementOutput builder) {}
//#else
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
//#endif
    }
}
