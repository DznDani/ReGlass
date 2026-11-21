//? if >=26.3 {
package restudio.reglass.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {

    @WrapMethod(method = "blurBeforeThisStratum")
    private void reglass$preventDuplicateBlur(Operation<Void> original) {
        try {
            original.call();
        } catch (IllegalStateException e) {
            if (e.getMessage() == null || !e.getMessage().contains("Can only blur once per frame")) {
                throw e;
            }
        }
    }
}
//?}
