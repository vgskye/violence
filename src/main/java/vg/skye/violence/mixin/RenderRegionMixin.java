package vg.skye.violence.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import grondag.canvas.terrain.region.RenderRegion;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vg.skye.violence.FlywheelHolder;

@Mixin(RenderRegion.class)
public class RenderRegionMixin {
    @WrapOperation(method = "addBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;"))
    private static <E extends BlockEntity> BlockEntityRenderer<E> addBlockEntityHandleBEChunkRebuild(BlockEntityRenderDispatcher instance, E blockEntity, Operation<BlockEntityRenderer<E>> original) {
        if (FlywheelHolder.handler.handleBEChunkRebuild(blockEntity)) {
            return original.call(instance, blockEntity);
        } else {
            return null;
        }
    }

}
