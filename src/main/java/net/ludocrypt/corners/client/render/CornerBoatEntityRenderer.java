package net.ludocrypt.corners.client.render;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.ludocrypt.corners.entity.CornerBoatEntity;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public final class CornerBoatEntityRenderer extends BoatRenderer {

	public CornerBoatEntityRenderer(EntityRendererProvider.Context context, boolean chest,
			CornerBoatEntity.CornerBoat boatData) {
		super(context, chest);
		var id = boatData.id();
		var texture = new ResourceLocation(id.getNamespace(),
			"textures/entity/" + (chest ? "chest_boat/" : "boat/") + id.getPath() + ".png");
		var rootPart = context.bakeLayer(getModelLayer(boatData, chest));
		var model = chest ? new ChestBoatModel(rootPart) : new BoatModel(rootPart);
		this.boatResources = this.boatResources
			.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> Pair.of(texture, model)));
	}

	public static ModelLayerLocation getModelLayer(CornerBoatEntity.CornerBoat boat, boolean chest) {
		var id = boat.id();
		return new ModelLayerLocation(new ResourceLocation(id.getNamespace(), (chest ? "chest_boat/" : "boat/") + id.getPath()),
			"main");
	}

}
