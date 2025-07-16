package net.ludocrypt.corners.mixin;

import net.ludocrypt.corners.entity.CornerBoatWithData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Boat.class)
public abstract class BoatEntityMixin extends Entity {

	public BoatEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyArg(method = "checkFallDamage", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/vehicle/Boat.spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"), order = 0)
	private ItemLike corners$modifyPlanks(ItemLike convertible) {

		if (this instanceof CornerBoatWithData boat) {
			return boat.getBoatData().planks();
		}

		return convertible;
	}

}
