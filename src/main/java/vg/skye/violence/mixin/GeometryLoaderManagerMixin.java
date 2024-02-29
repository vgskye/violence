package vg.skye.violence.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.models.CompositeModelLoader;
import io.github.fabricators_of_create.porting_lib.models.DynamicFluidContainerModel;
import io.github.fabricators_of_create.porting_lib.models.ElementsModel;
import io.github.fabricators_of_create.porting_lib.models.geometry.GeometryLoaderManager;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.obj.ObjLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vg.skye.violence.Violence;

import java.util.Map;

// Should this be necessary? No.
// Should this exist? No.
// Should everything work just fine without this? Yes.
// Does everything work just fine without this? No.
// I have no idea why this fixes things, but it does.
@Mixin(value = GeometryLoaderManager.class, remap = false)
public class GeometryLoaderManagerMixin {
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lio/github/fabricators_of_create/porting_lib/models/geometry/RegisterGeometryLoadersCallback;registerGeometryLoaders(Ljava/util/Map;)V"))
    private static void registerGeometryLoadersHardCoded(CallbackInfo ci, @Local Map<ResourceLocation, IGeometryLoader<?>> loaders) {
        loaders.put(ObjLoader.ID, ObjLoader.INSTANCE);
        loaders.put(PortingLib.id("elements"), ElementsModel.Loader.INSTANCE);
        loaders.put(PortingLib.id("composite"), CompositeModelLoader.INSTANCE);
        loaders.put(PortingLib.id("fluid_container"), DynamicFluidContainerModel.Loader.INSTANCE);
        loaders.put(new ResourceLocation("forge", "bucket"), DynamicFluidContainerModel.Loader.INSTANCE_DEPRECATED);
        Violence.LOGGER.warn("Registered the geometry loaders statically! This is a hack!");
    }
}
