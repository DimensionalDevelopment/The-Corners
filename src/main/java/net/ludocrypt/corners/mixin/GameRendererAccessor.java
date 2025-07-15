package net.ludocrypt.corners.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

	@Invoker
	double callGetFov(Camera camera, float tickDelta, boolean changingFov);

	@Invoker
	void callBobViewWhenHurt(PoseStack matrices, float tickDelta);

	@Invoker
	void callBobView(PoseStack matrices, float tickDelta);

}
