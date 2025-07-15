package net.ludocrypt.corners.world.maze;

import java.util.Map;

import com.google.common.collect.Maps;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class DecoratedMazeComponent extends MazeComponent {

	RandomSource random;

	public DecoratedMazeComponent(int width, int height, RandomSource random) {
		super(width, height);
		this.random = random;
	}

	@Override
	public void create() {
	}

	public static class Decoration {

		ResourceLocation id;
		Map<Direction, Byte[]> connections;

		public Decoration(ResourceLocation id, String connections) {
			this(id, parse(connections));
		}

		public Decoration(ResourceLocation id, Map<Direction, Byte[]> connections) {
			this.id = id;
			this.connections = connections;
		}

		public static Map<Direction, Byte[]> parse(String struct) {
			String[] cardinal = struct.split("_");
			Map<Direction, Byte[]> map = Maps.newHashMap();
			byte sec = 0;

			for (String section : cardinal) {
				Byte[] value = new Byte[section.length()];

				for (int i = 0; i < section.length(); i++) {
					value[i] = Byte.parseByte(String.valueOf(section.charAt(i)));
				}

				map.put(new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }[sec], value);
				sec++;
			}

			return map;
		}

		public ResourceLocation getId() {
			return id;
		}

		public Map<Direction, Byte[]> getConnections() {
			return connections;
		}

	}

}
