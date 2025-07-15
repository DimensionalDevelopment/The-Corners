package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.block.RailingBlock;
import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(method = "Lnet/minecraft/block/Block;shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
	private static void corners$shouldDrawSide(BlockState state, BlockGetter world, BlockPos pos, Direction side,
			BlockPos otherPos, CallbackInfoReturnable<Boolean> ci) {
		BlockState stateFrom = world.getBlockState(otherPos);

		if (side.getAxis() != Axis.Y) {

			if (state.is(CornerBlocks.DARK_RAILING)) {

				if (state.getValue(RailingBlock.LAYERS) > 0) {

					if (stateFrom.getBlock() instanceof RailingBlock) {
						ci.setReturnValue(stateFrom.getValue(RailingBlock.LAYERS) < state.getValue(RailingBlock.LAYERS));
					} else if (stateFrom.getBlock() instanceof SnowLayerBlock) {
						ci.setReturnValue(stateFrom.getValue(SnowLayerBlock.LAYERS) < state.getValue(RailingBlock.LAYERS));
					}

				}

			}

		}

	}

}
