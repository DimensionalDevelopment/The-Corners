package net.ludocrypt.corners.client.sound;

import net.ludocrypt.corners.access.MusicTrackerAccess;
import net.ludocrypt.corners.block.RadioBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class LoopingPositionedSoundInstance extends SimpleSoundInstance implements TickableSoundInstance {

	private final Level world;
	private final BlockPos pos;
	private boolean isDone = false;

	public LoopingPositionedSoundInstance(Level world, BlockPos pos, SoundEvent sound, SoundSource category, float volume,
			float pitch, RandomSource random, double x, double y, double z) {
		super(sound, category, volume, pitch, random, x, y, z);
		this.world = world;
		this.pos = pos;
	}

	public static void play(Level world, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch,
			RandomSource random, double x, double y, double z) {
		Minecraft client = Minecraft.getInstance();
		LoopingPositionedSoundInstance soundInstance = new LoopingPositionedSoundInstance(world, pos, sound, category,
			volume, pitch, random, x, y, z);
		soundInstance.looping = true;
		soundInstance.delay = 0;
		client.getSoundManager().play(soundInstance);
	}

	@Override
	public boolean isStopped() {
		return this.isDone;
	}

	@Override
	public void tick() {

		if (!(this.world.getBlockState(pos).getBlock() instanceof RadioBlock)) {
			this.isDone = true;
			((MusicTrackerAccess) (Minecraft.getInstance().getMusicManager())).getRadioPositions().remove(pos);
		}

	}

}
