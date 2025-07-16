package net.ludocrypt.corners.block;

import net.ludocrypt.corners.entity.CornerBoatEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class CornerBoatItem extends BoatItem {

	private final CornerBoatEntity.CornerBoat boatData;
	private final boolean chest;

	public CornerBoatItem(boolean chest, CornerBoatEntity.CornerBoat boatData, Item.Properties settings) {
		super(chest, Boat.Type.OAK, settings);
		this.chest = chest;
		this.boatData = boatData;
	}

	@Override
	public @NotNull Boat getBoat(Level world, HitResult hitResult) {
		var entity = boatData.factory(chest).create(boatData.entityType(chest), world);
		entity.absMoveTo(hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z);
		return entity;
	}

}
