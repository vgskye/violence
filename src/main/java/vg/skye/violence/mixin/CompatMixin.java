package vg.skye.violence.mixin;

import grondag.canvas.compat.Compat;
import grondag.canvas.render.world.CanvasWorldRenderer;
import grondag.canvas.render.world.WorldEventHelper;
import io.vram.frex.api.renderloop.EntityRenderPreListener;
import io.vram.frex.api.renderloop.FrustumSetupListener;
import io.vram.frex.api.renderloop.RenderReloadListener;
import io.vram.frex.api.renderloop.TranslucentPostListener;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vg.skye.violence.FlywheelHolder;
import vg.skye.violence.Violence;

@Mixin(value = Compat.class, remap = false)
public class CompatMixin {
    @Inject(method = "init", at = @At("HEAD"))
    private static void init(CallbackInfo ci) {
        Violence.LOGGER.info("Registering flywheel callbacks for Canvas!");
        FrustumSetupListener.register(ctx -> FlywheelHolder.handler.beginFrame(ctx.world(), ctx.camera(), ctx.frustum()));
        EntityRenderPreListener.register(ctx -> WorldEventHelper.useIdentityStack(ctx, () -> {
            FlywheelHolder.handler.renderLayer(ctx, RenderType.solid());
            FlywheelHolder.handler.renderLayer(ctx, RenderType.cutoutMipped());
            FlywheelHolder.handler.renderLayer(ctx, RenderType.cutout());
        }));
        TranslucentPostListener.register(ctx -> WorldEventHelper.useIdentityStack(ctx, () -> FlywheelHolder.handler.renderLayer(ctx, RenderType.translucent())));
        RenderReloadListener.register(() -> FlywheelHolder.handler.refresh(CanvasWorldRenderer.instance().worldRenderState.getWorld()));
    }
}
