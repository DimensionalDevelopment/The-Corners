package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.corners.init.CornerWorlds;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.util.Mth;

@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin {

	@Inject(method = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZF)V", at = @At("TAIL"))
	private static void corners$applyFog(Camera camera, FogMode fogType, float viewDistance, boolean thickFog,
			float tickDelta, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();
		float fogStart = RenderSystem.getShaderFogStart();
		float fogEnd = RenderSystem.getShaderFogEnd();

		if (client.level.dimension().equals(CornerWorlds.HOARY_CROSSROADS_KEY)) {
			fogStart = fogStart / 2;
			fogEnd = fogEnd / 2;
			float cameraHeight = (float) (camera.getPosition().y() - client.level.getMinBuildHeight());
			float fogScalar = (float) Mth
				.clamp(Math.atan(((cameraHeight - 263.0F) * Math.tan(1)) / 263.0F) + 1, 0.0F, 1.0F);
			RenderSystem.setShaderFogStart(fogStart * fogScalar);
			RenderSystem.setShaderFogEnd(fogEnd * fogScalar);
			RenderSystem.setShaderFogColor(0.625F, 0.625F, 0.625F);
		} else if (client.level.dimension().equals(CornerWorlds.COMMUNAL_CORRIDORS_KEY)) {
			RenderSystem.setShaderFogColor(0.244F, 0.244F, 0.244F);
		}

	}

}
