package restudio.reglass.mixin.client;

//#if MC >= 26
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#if MC >= 26.2
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.gui.contextualbar.ExperienceBar;
//#else
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
//#endif
import net.minecraft.client.player.LocalPlayer;
//#else
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
//#endif
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import restudio.reglass.client.LiquidGlassUniforms;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.runtime.XpBarGlassRenderer;

//#if MC >= 26.2
@Mixin(ExperienceBar.class)
//#elseif MC >= 26
@Mixin(ExperienceBarRenderer.class)
//#else
@Mixin(ExperienceBar.class)
//#endif
public abstract class ExperienceBarMixin {

//#if MC >= 26
    @Shadow @Final private Minecraft minecraft;
//#else
    @Shadow @Final private MinecraftClient client;
//#endif

    @Unique private static double reglass$smoothProgress = Double.NaN;
    @Unique private static int reglass$lastLevel = Integer.MIN_VALUE;
    @Unique private static double reglass$glowPulse = 0.0;
    @Unique private static int reglass$lastPlayerId = Integer.MIN_VALUE;

//#if MC >= 26
    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void reglass$renderExperienceBar(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
//#else
    @Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
    private void reglass$renderExperienceBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
//#endif
        ReGlassConfig cfg = ReGlassConfig.INSTANCE;
        if (!cfg.features.enableRedesign || !cfg.features.experienceBar) {
            return;
        }

//#if MC >= 26
        LocalPlayer player = this.minecraft.player;
//#else
        ClientPlayerEntity player = this.client.player;
//#endif
        if (player == null) {
            return;
        }

//#if MC >= 26
        int xpNeeded = player.getXpNeededForNextLevel();
//#else
        int xpNeeded = player.getNextLevelExperience();
//#endif
        if (xpNeeded <= 0) {
            return;
        }

        ci.cancel();

//#if MC >= 26.2
        int x = ((ContextualBar) (Object) this).left(this.minecraft.getWindow());
        int y = ((ContextualBar) (Object) this).top(this.minecraft.getWindow());
//#elseif MC >= 26
        int x = ((ContextualBarRenderer) (Object) this).left(this.minecraft.getWindow());
        int y = ((ContextualBarRenderer) (Object) this).top(this.minecraft.getWindow());
//#else
        int x = ((Bar) (Object) this).getCenterX(this.client.getWindow());
        int y = ((Bar) (Object) this).getCenterY(this.client.getWindow());
//#endif
        int width = 182;

        double target = Math.max(0.0, Math.min(1.0, player.experienceProgress));
        double dt = LiquidGlassUniforms.get().getFrameDeltaSeconds();
        int playerId = player.getId();

        if (playerId != reglass$lastPlayerId || Double.isNaN(reglass$smoothProgress)) {
            reglass$smoothProgress = target;
            reglass$lastLevel = player.experienceLevel;
            reglass$lastPlayerId = playerId;
            reglass$glowPulse = 0.0;
        } else if (player.experienceLevel != reglass$lastLevel) {
            if (player.experienceLevel > reglass$lastLevel) {
                reglass$glowPulse = 1.0;
            }
            reglass$smoothProgress = target;
            reglass$lastLevel = player.experienceLevel;
        } else {
            if (target > reglass$smoothProgress + 0.005) {
                reglass$glowPulse = Math.max(reglass$glowPulse, 0.45);
            }
            double alpha = 1.0 - Math.exp(-Math.max(0.0, dt) / 0.12);
            alpha = Math.max(0.0, Math.min(1.0, alpha));
            reglass$smoothProgress += (target - reglass$smoothProgress) * alpha;
            if (Math.abs(target - reglass$smoothProgress) < 0.0005) {
                reglass$smoothProgress = target;
            }
        }

        double pulseDecay = 1.0 - Math.exp(-Math.max(0.0, dt) / 0.35);
        reglass$glowPulse *= Math.max(0.0, 1.0 - pulseDecay);

        XpBarGlassRenderer.render(context, x, y, width, reglass$smoothProgress, reglass$glowPulse);
    }
}
