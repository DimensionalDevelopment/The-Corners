package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.ludocrypt.corners.entity.CornerBoatDispensorBehavior;
import net.ludocrypt.corners.entity.CornerBoatEntity;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.world.entity.vehicle.Boat;

@Mixin(BoatDispenseItemBehavior.class)
public class BoatDispenserBehaviorMixin {

	@ModifyVariable(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setVariant(Lnet/minecraft/world/entity/vehicle/Boat$Type;)V"), allow = 1)
	private Boat corners$modifyBoat(Boat original) {

		if ((Object) this instanceof CornerBoatDispensorBehavior boat) {
			return CornerBoatEntity.copy(original, boat.getBoatData());
		}

		return original;
	}

}
