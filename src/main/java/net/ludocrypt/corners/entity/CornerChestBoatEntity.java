package net.ludocrypt.corners.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public final class CornerChestBoatEntity extends ChestBoat implements CornerBoatWithData {

	private final CornerBoatEntity.CornerBoat boatData;

	public CornerChestBoatEntity(EntityType<? extends Boat> entityType, Level world,
			CornerBoatEntity.CornerBoat boatData) {
		super(entityType, world);
		this.boatData = boatData;
	}

	@Override
	public CornerBoatEntity.CornerBoat getBoatData() {
		return boatData;
	}

	@Override
	public Boat.Type getVariant() {
		return Boat.Type.OAK;
	}

	@Override
	public void setVariant(Boat.Type type) {
	}

	@Override
	public Item getDropItem() {
		return boatData.chestBoat().asItem();
	}

}
