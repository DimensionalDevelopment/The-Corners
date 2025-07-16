package net.ludocrypt.corners.client;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.ludocrypt.corners.client.render.CornerBoatEntityRenderer;
import net.ludocrypt.corners.entity.CornerBoatEntity.CornerBoat;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PaintingRenderer;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ServerToClientPackets.manageServerToClientPackets();
		BlockRenderLayerMap.INSTANCE.putBlocks(
			RenderType.cutout(), CornerBlocks.SNOWY_GLASS_PANE, CornerBlocks.SNOWY_GLASS,
				CornerBlocks.SNOWY_GLASS_SLAB, CornerBlocks.GAIA_DOOR, CornerBlocks.GAIA_TRAPDOOR, CornerBlocks.GAIA_SAPLING,
				CornerBlocks.POTTED_GAIA_SAPLING);
		EntityRendererRegistryImpl.register(CornerEntities.DIMENSIONAL_PAINTING_ENTITY, PaintingRenderer::new);
//		EntityRendererRegistryImpl.register(CornerEntities.CORVUS_ENTITY, CorvusEntityRenderer::new);
//		EntityModelLayerRegistry.registerModelLayer(CorvusEntityModel.LAYER_LOCATION, () -> CorvusEntityModel.createBodyLayer());
		EntityRendererRegistry
			.register(CornerBoat.GAIA.entityType(false),
				context -> new CornerBoatEntityRenderer(context, false, CornerBoat.GAIA));
		EntityModelLayerRegistry
			.registerModelLayer(CornerBoatEntityRenderer.getModelLayer(CornerBoat.GAIA, false),
				() -> BoatModel.createBodyModel());
		EntityRendererRegistry
			.register(CornerBoat.GAIA.entityType(true),
				context -> new CornerBoatEntityRenderer(context, true, CornerBoat.GAIA));
		EntityModelLayerRegistry
			.registerModelLayer(CornerBoatEntityRenderer.getModelLayer(CornerBoat.GAIA, true),
				() -> ChestBoatModel.createBodyModel());
	}

}
