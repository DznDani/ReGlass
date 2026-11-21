package restudio.reglass.mixin.widgets;

//#if MC >= 26
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
//#else
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import restudio.reglass.client.LiquidGlassUniforms;
import restudio.reglass.client.api.ReGlassApi;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.runtime.PlatformInput;
import restudio.reglass.extensions.PressableWidgetExtension;

//#if MC >= 26
@Mixin(AbstractButton.class)
public abstract class PressableWidgetMixin extends AbstractWidget implements PressableWidgetExtension {
//#else
@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin extends ClickableWidget implements PressableWidgetExtension {
//#endif
    @Unique
    private boolean reglass$isDragging;
    @Unique
    private int reglass$pressedButton = -1;
    @Unique
    private boolean reglass$doubleClick;
    @Unique
    private Object reglass$pressedScreen;

//#if MC >= 26
    protected PressableWidgetMixin(int x, int y, int width, int height, Component message) {
//#else
    protected PressableWidgetMixin(int x, int y, int width, int height, Text message) {
//#endif
        super(x, y, width, height, message);
    }

    @Unique
    private static boolean reglass$enabled() {
        return ReGlassConfig.INSTANCE.features.enableRedesign && ReGlassConfig.INSTANCE.features.buttons;
    }

    @Unique
    private static Object reglass$currentScreen() {
//#if MC >= 26.2
        return Minecraft.getInstance().gui.screen();
//#elseif MC >= 26
        return Minecraft.getInstance().screen;
//#else
        return MinecraftClient.getInstance().currentScreen;
//#endif
    }

    @Unique
    private void reglass$cancelPress() {
        this.reglass$isDragging = false;
        this.reglass$pressedButton = -1;
        this.reglass$pressedScreen = null;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            reglass$cancelPress();
        }
    }

    @Override
    public void reglass$beginPress(int button, boolean isDoubleClick) {
        this.reglass$isDragging = true;
        this.reglass$pressedButton = button;
        this.reglass$doubleClick = isDoubleClick;
        this.reglass$pressedScreen = reglass$currentScreen();
    }

    @Override
    public boolean reglass$canRelease(int button) {
        if (!this.reglass$isDragging || this.reglass$pressedButton == -1) {
            return false;
        }
        if (button != this.reglass$pressedButton) {
            return false;
        }
        return this.reglass$pressedScreen == reglass$currentScreen();
    }

    @Override
    public boolean reglass$isDoubleClick() {
        return this.reglass$doubleClick;
    }

    @Override
    public void reglass$setIsDragging(boolean isDragging) {
        if (!isDragging) {
            reglass$cancelPress();
        } else {
            this.reglass$isDragging = true;
        }
    }

    @Override
    public boolean reglass$isDragging() {
        if (!this.reglass$isDragging || this.reglass$pressedButton == -1) {
            return false;
        }
        // Clear a press if its release was lost while changing screens or window focus.
        if (!reglass$enabled() || !active || !visible || reglass$pressedScreen != reglass$currentScreen()
                || !PlatformInput.isWindowFocused()
                || !PlatformInput.isMouseButtonDown(reglass$pressedButton)) {
            reglass$cancelPress();
            return false;
        }
        return true;
    }

//#if MC >= 26
    @Inject(method = "extractDefaultSprite", at = @At("HEAD"), cancellable = true)
    private void reglass$renderButton(GuiGraphicsExtractor context, CallbackInfo ci) {
//#else
    @Inject(method = "drawButton", at = @At("HEAD"), cancellable = true)
    private void reglass$renderButton(DrawContext context, CallbackInfo ci) {
//#endif
        boolean dragging = reglass$isDragging();
        if (!reglass$enabled()) {
            return;
        }
        boolean isDisabled = !this.active;
        boolean isHighlighted = this.active && this.isHovered();
        boolean isFocused = isHighlighted && dragging;
        boolean tradeRow = ReGlassConfig.INSTANCE.features.containers && width >= 80 && width <= 100 && height == 20;

        ReGlassApi.create(context).fromWidget(this).text(null)
                .position(getX(), getY() + (tradeRow ? 1 : 0))
                .size(width, height - (tradeRow ? 2 : 0))
                .hover(isHighlighted ? 1f : 0f)
                .focus(isFocused ? 1.0f : 0.0f)
                .style(WidgetStyle.create().tint(isDisabled ? 0xFF000000 : 0xFFFFFFFF, isDisabled ? 0.4f : 0f))
                .render();

        LiquidGlassUniforms.get().tryApplyBlur(context);
        ci.cancel();
    }
}
