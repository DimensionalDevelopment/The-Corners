package net.ludocrypt.corners.client.render;

import org.joml.Matrix4f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.mixin.GameRendererAccessor;
import net.ludocrypt.specialmodels.api.SpecialModelRenderer;
import net.ludocrypt.specialmodels.impl.render.MutableQuad;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

public class SkyboxRenderer extends SpecialModelRenderer {

	private final String id;

	public SkyboxRenderer(String id) {
		this.id = id;
	}

	@Override
	@ClientOnly
	public void setup(PoseStack matrices, Matrix4f viewMatrix, Matrix4f positionMatrix, float tickDelta,
			ShaderInstance shader, BlockPos origin) {

		for (int i = 0; i < 6; i++) {
			RenderSystem.setShaderTexture(i, TheCorners.id("textures/sky/" + id + "_" + i + ".png"));
		}

		Minecraft client = Minecraft.getInstance();
		Camera camera = client.gameRenderer.getMainCamera();
		Matrix4f matrix = new PoseStack().last().pose();
		matrix.rotate(Axis.XP.rotationDegrees(camera.getXRot()));
		matrix.rotate(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));

		if (shader.getUniform("RotMat") != null) {
			shader.getUniform("RotMat").set(matrix);
		}

		PoseStack matrixStack = new PoseStack();
		((GameRendererAccessor) client.gameRenderer).callBobViewWhenHurt(matrixStack, tickDelta);

		if (client.options.bobView().get()) {
			((GameRendererAccessor) client.gameRenderer).callBobView(matrixStack, tickDelta);
		}

		if (shader.getUniform("bobMat") != null) {
			shader.getUniform("bobMat").set(matrixStack.last().pose());
		}

	}

	@Override
	@ClientOnly
	public MutableQuad modifyQuad(RenderChunkRegion chunkRenderRegion, BlockPos pos, BlockState state, BakedModel model,
			BakedQuad quadIn, long modelSeed, MutableQuad quad) {
		quad.getV1().setUv(new Vec2(0.0F, 0.0F));
		quad.getV2().setUv(new Vec2(0.0F, 1.0F));
		quad.getV3().setUv(new Vec2(1.0F, 1.0F));
		quad.getV4().setUv(new Vec2(1.0F, 0.0F));
		return quad;
	}

}
