package net.ludocrypt.corners.entity;

import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.world.entity.vehicle.Boat;

public final class CornerBoatDispensorBehavior extends BoatDispenseItemBehavior {

	private final CornerBoatEntity.CornerBoat boatData;

	public CornerBoatDispensorBehavior(CornerBoatEntity.CornerBoat boatData, boolean chest) {
		super(Boat.Type.OAK, chest);
		this.boatData = boatData;
	}

	public CornerBoatEntity.CornerBoat getBoatData() {
		return boatData;
	}

}
