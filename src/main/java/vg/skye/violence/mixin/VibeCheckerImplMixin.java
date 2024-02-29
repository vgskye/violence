package vg.skye.violence.mixin;

import net.mehvahdjukaar.supplementaries.common.utils.fabric.VibeCheckerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VibeCheckerImpl.class, remap = false)
public class VibeCheckerImplMixin {
    @Inject(method = "checkVibe", at = @At("HEAD"), cancellable = true)
    private static void dontCheckVibe(CallbackInfo ci) {
        // vibe check uses dummy texture atlas to check baking
        // FREX doesn't work with dummy text atlases
        ci.cancel();
    }
}
