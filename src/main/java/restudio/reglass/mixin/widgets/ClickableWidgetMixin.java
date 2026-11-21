package restudio.reglass.mixin.widgets;

//#if MC >= 26
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
//#else
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.sound.SoundManager;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.extensions.PressableWidgetExtension;

//#if MC >= 26
@Mixin(AbstractWidget.class)
//#else
@Mixin(ClickableWidget.class)
//#endif
public abstract class ClickableWidgetMixin {
    @Shadow
    public boolean active;

    @Shadow
    public boolean visible;

    @Shadow
    public abstract boolean isMouseOver(double mouseX, double mouseY);

    @Shadow
    public abstract void playDownSound(SoundManager soundManager);

//#if MC >= 26
    @Shadow
    public abstract void onClick(MouseButtonEvent click, boolean isDouble);
//#else
    @Shadow
    public abstract void onClick(Click click, boolean isDouble);
//#endif

    @Unique
    private static boolean reglass$buttonsEnabled() {
        return ReGlassConfig.INSTANCE.features.enableRedesign && ReGlassConfig.INSTANCE.features.buttons;
    }

    @Unique
    private boolean reglass$isPressable() {
//#if MC >= 26
        return (Object) this instanceof AbstractButton;
//#else
        return (Object) this instanceof PressableWidget;
//#endif
    }

//#if MC >= 26
    @Redirect(
            method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/AbstractWidget;playDownSound(Lnet/minecraft/client/sounds/SoundManager;)V"
            )
    )
    private void redirectPlayDownSound(AbstractWidget instance, SoundManager soundManager) {
//#else
    @Redirect(
            method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/ClickableWidget;playDownSound(Lnet/minecraft/client/sound/SoundManager;)V"
            )
    )
    private void redirectPlayDownSound(ClickableWidget instance, SoundManager soundManager) {
//#endif
        if (reglass$isPressable() && reglass$buttonsEnabled()) {
            return;
        }
        playDownSound(soundManager);
    }

//#if MC >= 26
    @Redirect(
            method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/AbstractWidget;onClick(Lnet/minecraft/client/input/MouseButtonEvent;Z)V"
            )
    )
    private void redirectOnClick(AbstractWidget instance, MouseButtonEvent click, boolean isDouble) {
//#else
    @Redirect(
            method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/ClickableWidget;onClick(Lnet/minecraft/client/gui/Click;Z)V"
            )
    )
    private void redirectOnClick(ClickableWidget instance, Click click, boolean isDouble) {
//#endif
        if (reglass$isPressable() && reglass$buttonsEnabled()) {
            ((PressableWidgetExtension) this).reglass$beginPress(click.button(), isDouble);
            return;
        }
        onClick(click, isDouble);
    }

//#if MC >= 26
    @Inject(
            method = "mouseReleased(Lnet/minecraft/client/input/MouseButtonEvent;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/AbstractWidget;onRelease(Lnet/minecraft/client/input/MouseButtonEvent;)V"
            )
    )
    private void modifyOnRelease(MouseButtonEvent click, CallbackInfoReturnable<Boolean> cir) {
//#else
    @Inject(
            method = "mouseReleased(Lnet/minecraft/client/gui/Click;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/ClickableWidget;onRelease(Lnet/minecraft/client/gui/Click;)V"
            )
    )
    private void modifyOnRelease(Click click, CallbackInfoReturnable<Boolean> cir) {
//#endif
        if (reglass$isPressable() && this instanceof PressableWidgetExtension ext) {
            if (!ext.reglass$canRelease(click.button())) {
                return;
            }
            boolean isDouble = ext.reglass$isDoubleClick();
            boolean shouldActivate = reglass$buttonsEnabled() && this.active && this.visible && this.isMouseOver(click.x(), click.y());
            ext.reglass$setIsDragging(false);
            if (shouldActivate) {
//#if MC >= 26
                this.playDownSound(Minecraft.getInstance().getSoundManager());
//#else
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
//#endif
                this.onClick(click, isDouble);
            }
        }
    }
}
