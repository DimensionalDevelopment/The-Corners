package net.ludocrypt.corners.client.entity.corvus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.covrus.CorvusEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CorvusEntityModel<T extends CorvusEntity> extends HierarchicalModel<T> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TheCorners.id("corvus"), "main");
	private final ModelPart main;

	public CorvusEntityModel(ModelPart root) {
		this.main = root.getChild("main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition main = modelPartData
			.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition body = main
			.addOrReplaceChild("body",
				CubeListBuilder
					.create()
					.texOffs(0, 0)
					.addBox(-3.0F, -5.0F, -6.0F, 6.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
					.texOffs(27, 36)
					.addBox(-3.0F, -5.5F, -6.5F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
					.texOffs(16, 14)
					.addBox(-2.0F, -5.0F, 2.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -4.1F, -0.6F, -0.8727F, 0.0F, 0.0F));
		PartDefinition left_wing = body
			.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(3.5F, -2.9F, -4.8F));
		PartDefinition cube_r1 = left_wing
			.addOrReplaceChild("cube_r1",
				CubeListBuilder
					.create()
					.texOffs(2, 28)
					.addBox(1.8F, -8.0F, -5.8F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
					.texOffs(5, 32)
					.addBox(1.8F, -6.0F, -5.8F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(5.2F, -2.5F, 7.2F, 1.5708F, 0.0F, 1.5708F));
		PartDefinition left_wing_2 = left_wing
			.addOrReplaceChild("left_wing_2", CubeListBuilder.create(), PartPose.offset(3.4F, -0.7F, -0.8F));
		PartDefinition left_wing_end_r1 = left_wing_2
			.addOrReplaceChild("left_wing_end_r1",
				CubeListBuilder.create().texOffs(2, 38).addBox(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(2.0F, 0.4F, 0.0F, 1.5708F, 0.0F, 1.5708F));
		PartDefinition cube_r2 = left_wing_2
			.addOrReplaceChild("cube_r2",
				CubeListBuilder.create().texOffs(26, 0).addBox(1.8F, -8.0F, -7.8F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(7.8F, -1.8F, 8.0F, 1.5708F, 0.0F, 1.5708F));
		PartDefinition left_wing_mid_r1 = left_wing_2
			.addOrReplaceChild("left_wing_mid_r1",
				CubeListBuilder.create().texOffs(8, 31).addBox(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 1.5708F, 0.0F, 1.5708F));
		PartDefinition right_wing = body
			.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-3.5F, -2.9F, -4.8F));
		PartDefinition cube_r3 = right_wing
			.addOrReplaceChild("cube_r3",
				CubeListBuilder
					.create()
					.texOffs(2, 28)
					.mirror()
					.addBox(-3.8F, -8.0F, -5.8F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
					.mirror(false)
					.texOffs(5, 32)
					.mirror()
					.addBox(-1.8F, -6.0F, -5.8F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
					.mirror(false),
				PartPose.offsetAndRotation(-5.2F, -2.5F, 7.2F, 1.5708F, 0.0F, -1.5708F));
		PartDefinition right_wing_2 = right_wing
			.addOrReplaceChild("right_wing_2", CubeListBuilder.create(), PartPose.offset(-3.4F, -0.7F, -0.8F));
		PartDefinition right_wing_end_r1 = right_wing_2
			.addOrReplaceChild("right_wing_end_r1",
				CubeListBuilder
					.create()
					.texOffs(2, 38)
					.mirror()
					.addBox(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
					.mirror(false),
				PartPose.offsetAndRotation(-2.0F, 0.4F, 0.0F, 1.5708F, 0.0F, -1.5708F));
		PartDefinition cube_r4 = right_wing_2
			.addOrReplaceChild("cube_r4",
				CubeListBuilder
					.create()
					.texOffs(26, 0)
					.mirror()
					.addBox(-2.8F, -8.0F, -7.8F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
					.mirror(false),
				PartPose.offsetAndRotation(-7.8F, -1.8F, 8.0F, 1.5708F, 0.0F, -1.5708F));
		PartDefinition right_wing_mid_r1 = right_wing_2
			.addOrReplaceChild("right_wing_mid_r1",
				CubeListBuilder
					.create()
					.texOffs(8, 31)
					.mirror()
					.addBox(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
					.mirror(false),
				PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 1.5708F, 0.0F, -1.5708F));
		PartDefinition head_animation = body
			.addOrReplaceChild("head_animation", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, -3.9F, -5.9F, -0.6545F, 0.0F, 0.0F));
		PartDefinition head = head_animation
			.addOrReplaceChild("head",
				CubeListBuilder
					.create()
					.texOffs(12, 23)
					.addBox(-2.0F, -1.7F, -3.2F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
					.texOffs(0, 0)
					.addBox(-1.0F, 3.3F, -2.6F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.9F, 0.6F));
		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -4.4F, 2.0F));
		PartDefinition right_tail = tail
			.addOrReplaceChild("right_tail",
				CubeListBuilder.create().texOffs(14, 0).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, -0.4363F, 0.0F));
		PartDefinition left_tail = tail
			.addOrReplaceChild("left_tail",
				CubeListBuilder.create().texOffs(20, 0).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.4363F, 0.0F));
		PartDefinition left_leg = main
			.addOrReplaceChild("left_leg",
				CubeListBuilder
					.create()
					.texOffs(48, 23)
					.addBox(-1.0F, -1.5F, -0.9F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
					.texOffs(0, 25)
					.addBox(-0.5F, -0.5F, -0.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.7F, -2.5F, 0.2F, 0.0F, -0.2182F, 0.0F));
		PartDefinition cube_r5 = left_leg
			.addOrReplaceChild("cube_r5",
				CubeListBuilder.create().texOffs(29, 27).addBox(-1.5F, 0.5F, -1.2F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.5F, 0.1F, -0.6981F, 0.0F, 0.0F));
		PartDefinition right_leg = main
			.addOrReplaceChild("right_leg",
				CubeListBuilder
					.create()
					.texOffs(48, 23)
					.mirror()
					.addBox(-1.0F, -1.5F, -0.9F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
					.mirror(false)
					.texOffs(0, 25)
					.mirror()
					.addBox(-0.5F, -0.5F, -0.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
					.mirror(false),
				PartPose.offsetAndRotation(-1.7F, -2.5F, 0.2F, 0.0F, 0.2182F, 0.0F));
		PartDefinition cube_r6 = right_leg
			.addOrReplaceChild("cube_r6",
				CubeListBuilder
					.create()
					.texOffs(29, 27)
					.mirror()
					.addBox(-1.5F, 0.5F, -1.2F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
					.mirror(false),
				PartPose.offsetAndRotation(0.0F, 1.5F, 0.1F, -0.6981F, 0.0F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void setupAnim(T corvus, float limbAngle, float limbDistance, float animationProgress, float headYaw,
			float headPitch) {
		this.main.getAllParts().forEach(ModelPart::resetPose);
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green,
			float blue, float alpha) {
		this.main.render(matrices, vertices, light, overlay);
	}

	@Override
	public ModelPart root() {
		return this.main;
	}
}
