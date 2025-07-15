package net.ludocrypt.corners.client.entity.corvus;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.covrus.CorvusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CorvusEntityRenderer extends MobRenderer<CorvusEntity, CorvusEntityModel<CorvusEntity>> {

	public CorvusEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new CorvusEntityModel<>(context.bakeLayer(CorvusEntityModel.LAYER_LOCATION)), 0.5F);
	}

	@Override
	public ResourceLocation getTexture(CorvusEntity entity) {
		return TheCorners.id("textures/entity/corvus/plain.png");
	}

}
