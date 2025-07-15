package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin<T extends BlockEntity> {

	@SuppressWarnings("unchecked")
	@Inject(method = "Lnet/minecraft/block/entity/BlockEntityType;supports(Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
	private void corners$supports(BlockState state, CallbackInfoReturnable<Boolean> ci) {
		ResourceLocation id = BlockEntityType.getKey((BlockEntityType<T>) ((Object) this));

		if (id.equals(BlockEntityType.getKey(BlockEntityType.SIGN)) || id
			.equals(BlockEntityType.getKey(BlockEntityType.HANGING_SIGN))) {

			if (state.getBlock() == CornerBlocks.GAIA_SIGN || state.getBlock() == CornerBlocks.GAIA_HANGING_SIGN || state
				.getBlock() == CornerBlocks.GAIA_WALL_HANGING_SIGN || state.getBlock() == CornerBlocks.GAIA_WALL_SIGN) {
				ci.setReturnValue(true);
			}

		} else if (id.equals(BlockEntityType.getKey(BlockEntityType.CHISELED_BOOKSHELF))) {

			if (state.is(CornerBlocks.DEEP_BOOKSHELF)) {
				ci.setReturnValue(true);
			}

		}

	}

}
