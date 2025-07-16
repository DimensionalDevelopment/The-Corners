package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SnowLayerBlock.class)
public class SnowBlockMixin {

	@Inject(method = "canBeReplaced", at = @At("RETURN"), cancellable = true)
	private void corners$canReplace(BlockState state, BlockPlaceContext ctx, CallbackInfoReturnable<Boolean> ci) {

		if (ctx.getItemInHand() != null) {

			if (ctx.getItemInHand().getItem().equals(CornerBlocks.DARK_RAILING.asItem())) {
				ci.setReturnValue(true);
			}

		}

	}

}
