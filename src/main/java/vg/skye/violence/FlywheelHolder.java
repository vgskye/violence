package vg.skye.violence;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.backend.gl.GlStateTracker;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderDispatcher;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.jozufozu.flywheel.event.BeginFrameEvent;
import com.jozufozu.flywheel.event.ReloadRenderersEvent;
import com.jozufozu.flywheel.event.RenderLayerEvent;
import com.jozufozu.flywheel.fabric.event.FlywheelEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import grondag.canvas.CanvasMod;
import grondag.canvas.mixinterface.LevelRendererExt;
import grondag.canvas.shader.data.MatrixData;
import io.vram.frex.api.renderloop.WorldRenderContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

// Largely copied from https://github.com/vram-guild/canvas/blob/1.19/src/main/java/grondag/canvas/compat/FlywheelHolder.java
public class FlywheelHolder {
    private static final FlywheelHandler DEFAULT_HANDLER = new EmptyHandler();
    public static FlywheelHandler handler = DEFAULT_HANDLER;

    static {
        if (FabricLoader.getInstance().isModLoaded("flywheel")) {
            try {
                compatibleInit(); // boring modCompileOnly method
                // reflectedInit(); // the alternative method
            } catch (final Throwable e) {
                // what are the odds?
                closeHandler(e);
            }
        }
    }

    private static void compatibleInit() {
        handler = new CompatibleHandler();
    }

    // since it's a class it can be debugged comfortably
    private static class CompatibleHandler implements FlywheelHandler {
        @Override
        public void beginFrame(ClientLevel level, Camera camera, Frustum frustum) {
            try {
                FlywheelEvents.BEGIN_FRAME.invoker().handleEvent(new BeginFrameEvent(level, camera, frustum));
            } catch (final Throwable e) {
                closeHandler(e);
            }
        }

        @Override
        public void renderLayer(LevelRenderer renderer, RenderType type, PoseStack stack, double camX, double camY, double camZ) {
            try {
                final LevelRendererExt ext = (LevelRendererExt) renderer;
                RenderBuffers renderBuffers = ext.canvas_bufferBuilders();
                GlStateTracker.State restoreState = GlStateTracker.getRestoreState();

                // RenderLayerEvent computes the View-Projection matrix during construction
                stack.pushPose();
                stack.last().pose().set(MatrixData.viewMatrix);
                var event = new RenderLayerEvent(ext.canvas_world(), type, stack, renderBuffers, camX, camY, camZ);
                stack.popPose();

                FlywheelEvents.RENDER_LAYER.invoker().handleEvent(event);

                restoreState.restore();
                // CanvasMod.LOG.warn("flywheel thing is invoked");
            } catch (final Throwable e) {
                closeHandler(e);
            }
        }

        @Override
        public void refresh(ClientLevel level) {
            Backend.refresh();
            FlywheelEvents.RELOAD_RENDERERS.invoker().handleEvent(new ReloadRenderersEvent(level));
        }

        @Override
        public boolean handleBEChunkRebuild(BlockEntity be) {
            if (Backend.canUseInstancing(be.getLevel())) {
                if (InstancedRenderRegistry.canInstance(be.getType())) {
                    InstancedRenderDispatcher.getBlockEntities(be.getLevel()).queueAdd(be);
                }

                return !InstancedRenderRegistry.shouldSkipRender(be);
            }

            return true;
        }
    }

    private static void closeHandler(Throwable e) {
        Violence.LOGGER.warn("Unable to defer to Flywheel due to exception: ", e);
        Violence.LOGGER.warn("Subsequent errors will be suppressed");
        handler = DEFAULT_HANDLER;
    }

    public interface FlywheelHandler {
        void beginFrame(ClientLevel level, Camera camera, Frustum frustum);

        default void renderLayer(WorldRenderContext ctx, RenderType type) {
            final var cam = ctx.camera().getPosition();
            renderLayer(ctx.worldRenderer(), type, ctx.poseStack(), cam.x, cam.y, cam.z);
        }

        void renderLayer(LevelRenderer renderer, RenderType type, PoseStack stack, double camX, double camY, double camZ);
        void refresh(ClientLevel level);

        boolean handleBEChunkRebuild(BlockEntity be);
    }

    private static final class EmptyHandler implements FlywheelHandler {
        @Override
        public void beginFrame(ClientLevel level, Camera camera, Frustum frustum) {
        }

        @Override
        public void renderLayer(WorldRenderContext ctx, RenderType type) {
        }

        @Override
        public void renderLayer(LevelRenderer renderer, RenderType type, PoseStack stack, double camX, double camY, double camZ) {
        }

        @Override
        public void refresh(ClientLevel level) {
        }

        @Override
        public boolean handleBEChunkRebuild(BlockEntity be) {
            return true;
        }
    }
}