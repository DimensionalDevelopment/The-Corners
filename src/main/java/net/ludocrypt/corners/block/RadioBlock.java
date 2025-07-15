package net.ludocrypt.corners.block;

import java.util.Map;

import javax.annotation.Nullable;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import com.google.common.collect.Maps;

import net.ludocrypt.corners.packet.ClientToServerPackets;
import net.ludocrypt.corners.world.feature.GaiaSaplingGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class RadioBlock extends HorizontalDirectionalBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final Map<Item, RadioBlock> CORES = Maps.newHashMap();
	public final @Nullable Item core;
	public final @Nullable RadioBlock empty;

	public RadioBlock(@Nullable Item core, @Nullable RadioBlock empty, Properties settings) {
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
		this.core = core;
		this.empty = empty;

		if (core != null && empty != null) {
			CORES.put(core, this);
		}

	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED, FACING);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {

		if (world.hasNeighborSignal(pos)) {
			sendOut(world, pos, !state.getValue(POWERED));
			world.setBlockAndUpdate(pos, state.cycle(POWERED));
			return InteractionResult.SUCCESS;
		} else if (player.getItemInHand(hand).is(Items.BONE_MEAL)) {

			if (this.core == null) {

				if (!world.isClientSide()) {

					if (!player.getAbilities().instabuild) {
						player.getItemInHand(hand).shrink(1);
					}

					if (this.empty != null) {
						world.setBlockAndUpdate(pos, of(state, empty));
					} else {

						if (world instanceof ServerLevel s) {
							new GaiaSaplingGenerator()
								.generateRadio(s, s.getChunkSource().getGenerator(), pos, state, s.getRandom());
						}

					}

					world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0);
				}

				return InteractionResult.SUCCESS;
			}

		} else if (hit.getDirection().equals(state.getValue(FACING))) {

			if (core != null && empty != null) {

				if (!world.isClientSide()) {
					player.getInventory().placeItemBackInInventory(core.getDefaultInstance());
					world.setBlockAndUpdate(pos, of(state, empty));
				}

				world.playLocalSound(pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F, false);
				return InteractionResult.SUCCESS;
			} else if (CORES.containsKey(player.getItemInHand(hand).getItem())) {

				if (!world.isClientSide()) {
					world.setBlockAndUpdate(pos, of(state, CORES.get(player.getItemInHand(hand).getItem())));

					if (!player.getAbilities().instabuild) {
						player.getItemInHand(hand).shrink(1);
					}

				}

				world.playLocalSound(pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 0.5F, false);
				return InteractionResult.SUCCESS;
			}

		}

		return InteractionResult.PASS;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BlockState of(BlockState from, Block to) {
		BlockState newState = to.defaultBlockState();

		for (Property p : from.getProperties()) {

			if (newState.getProperties().contains(p)) {
				newState = newState.setValue(p, from.getValue(p));
			}

		}

		return newState;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {

		if (world.hasNeighborSignal(pos)) {

			if (!state.getValue(POWERED)) {
				sendOut(world, pos, true);
			}

			world.setBlockAndUpdate(pos, state.setValue(POWERED, true));
		} else {

			if (state.getValue(POWERED)) {
				sendOut(world, pos, false);
			}

			world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
		}

		super.neighborChanged(state, world, pos, block, fromPos, notify);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		boolean power = ctx.getLevel().hasNeighborSignal(ctx.getClickedPos());

		if (power) {
			sendOut(ctx.getLevel(), ctx.getClickedPos(), true);
		}

		return super.getStateForPlacement(ctx).setValue(POWERED, power).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return state.getValue(POWERED);
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return hasAnalogOutputSignal(state) ? 1 : 0;
	}

	public static void sendOut(Level world, BlockPos pos, boolean powered) {

		if (!world.isClientSide) {
			FriendlyByteBuf buf = PacketByteBufs.create();
			buf.writeBlockPos(pos);
			buf.writeBoolean(powered);

			for (ServerPlayer serverPlayer : PlayerLookup.tracking((ServerLevel) world, pos)) {
				ServerPlayNetworking.send(serverPlayer, ClientToServerPackets.PLAY_RADIO, buf);
			}

		}

	}

}
