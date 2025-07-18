package net.ludocrypt.corners.init;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.*;
import net.ludocrypt.corners.entity.CornerBoatDispensorBehavior;
import net.ludocrypt.corners.entity.CornerBoatEntity.CornerBoat;
import net.ludocrypt.corners.mixin.SignTypeAccessor;
import net.ludocrypt.corners.world.feature.GaiaSaplingGenerator;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

public class CornerBlocks {

	public static final Block STONE_PILLAR = get("stone_pillar",
		new ThinPillarBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)));
	public static final RadioBlock GROWN_RADIO = get("grown_radio",
		new RadioBlock(null, null, FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
	public static final RadioBlock BROKEN_RADIO = get("broken_radio",
		new RadioBlock(null, GROWN_RADIO, FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
	public static final RadioBlock WOODEN_RADIO = get("wooden_radio",
		new RadioBlock(Items.GOLD_INGOT, BROKEN_RADIO, FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
	public static final RadioBlock TUNED_RADIO = get("tuned_radio",
		new RadioBlock(Items.AMETHYST_SHARD, BROKEN_RADIO, FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
	public static final Block DRYWALL = get("drywall", new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));
	public static final Block NYLON_FIBER_BLOCK = get("nylon_fiber_block",
		new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
	public static final Block NYLON_FIBER_STAIRS = get("nylon_fiber_stairs",
		new CornerStairsBlock(NYLON_FIBER_BLOCK.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
	public static final Block NYLON_FIBER_SLAB = get("nylon_fiber_slab",
		new SlabBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
	public static final Block SNOWY_GLASS = get("snowy_glass",
		new SkyboxGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).luminance(3)));
	public static final Block SNOWY_GLASS_PANE = get("snowy_glass_pane",
		new SkyboxGlassPaneBlock(FabricBlockSettings.copyOf(Blocks.GLASS_PANE).luminance(3)));
	public static final Block SNOWY_GLASS_SLAB = get("snowy_glass_slab",
		new SkyboxGlassSlabBlock(FabricBlockSettings.copyOf(Blocks.GLASS).luminance(3)));
	public static final Block DARK_RAILING = get("dark_railing", new RailingBlock(FabricBlockSettings.copyOf(Blocks.STONE)));
	public static final Block DEEP_BOOKSHELF = get("deep_bookshelf",
		new ChiseledBookShelfBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
	// Gaia
	public static final BlockSetType GAIA_SET_TYPE = BlockSetTypeBuilder
		.copyOf(BlockSetType.SPRUCE)
		.build(TheCorners.id("gaia"));
	public static final WoodType GAIA_SIGN_TYPE = SignTypeAccessor
		.callRegister(new WoodType("corners:gaia", BlockSetType.SPRUCE));
	public static final Block GAIA_PLANKS = get("gaia_planks", new Block(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS)));
	public static final Block CARVED_GAIA = get("carved_gaia",
		new OrientableBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS)));
	public static final Block GAIA_SAPLING = get("gaia_sapling", new SaplingBlock(new GaiaSaplingGenerator(),
		FabricBlockSettings.copyOf(Blocks.SPRUCE_SAPLING).mapColor(MapColor.GOLD)));
	public static final Block GAIA_LOG = get("gaia_log",
		new RotatedPillarBlock(FabricBlockSettings
			.copyOf(Blocks.SPRUCE_LOG)
			.mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.PODZOL : MapColor.GOLD)));
	public static final Block STRIPPED_GAIA_LOG = get("stripped_gaia_log",
		new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_SPRUCE_LOG).mapColor(MapColor.PODZOL)));
	public static final Block GAIA_WOOD = get("gaia_wood",
		new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_WOOD).mapColor(MapColor.GOLD)));
	public static final Block STRIPPED_GAIA_WOOD = get("stripped_gaia_wood",
		new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_SPRUCE_WOOD)));
	public static final Block GAIA_LEAVES = get("gaia_leaves",
		new LeavesBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_LEAVES)));
	public static final Block GAIA_SIGN = getSingle("gaia_sign",
		new StandingSignBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_SIGN), GAIA_SIGN_TYPE));
	public static final Block GAIA_WALL_SIGN = getSingle("gaia_wall_sign",
		new WallSignBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_WALL_SIGN).dropsLike(GAIA_SIGN), GAIA_SIGN_TYPE));
	public static final Block GAIA_HANGING_SIGN = getSingle("gaia_hanging_sign",
		new CeilingHangingSignBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_HANGING_SIGN), GAIA_SIGN_TYPE));
	public static final Block GAIA_WALL_HANGING_SIGN = getSingle("gaia_wall_hanging_sign", new WallHangingSignBlock(
		FabricBlockSettings.copyOf(Blocks.SPRUCE_WALL_HANGING_SIGN).dropsLike(GAIA_HANGING_SIGN), GAIA_SIGN_TYPE));
	public static final Block GAIA_PRESSURE_PLATE = get("gaia_pressure_plate",
		new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
			FabricBlockSettings.copyOf(Blocks.SPRUCE_PRESSURE_PLATE), GAIA_SET_TYPE));
	public static final Block GAIA_TRAPDOOR = get("gaia_trapdoor",
		new TrapDoorBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_TRAPDOOR), GAIA_SET_TYPE));
	public static final Block POTTED_GAIA_SAPLING = getSingle("potted_gaia_sapling",
		Blocks.flowerPot(GAIA_SAPLING));
	public static final Block GAIA_BUTTON = get("gaia_button", Blocks.woodenButton(GAIA_SET_TYPE));
	public static final Block GAIA_STAIRS = get("gaia_stairs",
		new StairBlock(GAIA_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(GAIA_PLANKS)));
	public static final Block GAIA_SLAB = get("gaia_slab", new SlabBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_SLAB)));
	public static final Block GAIA_FENCE_GATE = get("gaia_fence_gate",
		new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_FENCE_GATE), GAIA_SIGN_TYPE));
	public static final Block GAIA_FENCE = get("gaia_fence", new FenceBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_FENCE)));
	public static final Block GAIA_DOOR = getSingle("gaia_door",
		new DoorBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_DOOR), GAIA_SET_TYPE));
	public static final Item GAIA_BOAT = get("gaia_boat",
		new CornerBoatItem(false, CornerBoat.GAIA, new Item.Properties().stacksTo(1)));
	public static final Item GAIA_CHEST_BOAT = get("gaia_chest_boat",
		new CornerBoatItem(true, CornerBoat.GAIA, new Item.Properties().stacksTo(1)));
	public static final Item GAIA_SIGN_ITEM = get("gaia_sign",
		new SignItem(new Item.Properties().stacksTo(16), GAIA_SIGN, GAIA_WALL_SIGN));
	public static final Item GAIA_HANGING_SIGN_ITEM = get("gaia_hanging_sign",
		new HangingSignItem(GAIA_HANGING_SIGN, GAIA_WALL_HANGING_SIGN, new Item.Properties().stacksTo(16)));
	public static final Item GAIA_DOOR_ITEM = get("gaia_door", new DoubleHighBlockItem(GAIA_DOOR, new Item.Properties()));

	public static void init() {
		DispenserBlock.registerBehavior(GAIA_BOAT, new CornerBoatDispensorBehavior(CornerBoat.GAIA, false));
		DispenserBlock.registerBehavior(GAIA_CHEST_BOAT, new CornerBoatDispensorBehavior(CornerBoat.GAIA, true));

		FlammableBlockRegistry.getDefaultInstance().add(NYLON_FIBER_BLOCK, 30, 60);
		FlammableBlockRegistry.getDefaultInstance().add(NYLON_FIBER_STAIRS, 30, 60);
		FlammableBlockRegistry.getDefaultInstance().add(NYLON_FIBER_SLAB, 30, 60);
		FlammableBlockRegistry.getDefaultInstance().add(DRYWALL, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(WOODEN_RADIO, 10, 20);
		FlammableBlockRegistry.getDefaultInstance().add(TUNED_RADIO, 10, 20);
		FlammableBlockRegistry.getDefaultInstance().add(BROKEN_RADIO, 10, 20);
		FlammableBlockRegistry.getDefaultInstance().add(GROWN_RADIO, 10, 20);
		FlammableBlockRegistry.getDefaultInstance().add(STRIPPED_GAIA_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(STRIPPED_GAIA_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(GAIA_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(GAIA_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(GAIA_STAIRS, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(GAIA_SLAB, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(GAIA_PLANKS, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(CARVED_GAIA, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(GAIA_FENCE, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(GAIA_FENCE_GATE, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(GAIA_LEAVES, 30, 60);

		FuelRegistry.INSTANCE.add(GAIA_FENCE, 300);
		FuelRegistry.INSTANCE.add(GAIA_FENCE_GATE, 300);
		FuelRegistry.INSTANCE.add(NYLON_FIBER_BLOCK, 100);
		FuelRegistry.INSTANCE.add(NYLON_FIBER_STAIRS, 100);
		FuelRegistry.INSTANCE.add(NYLON_FIBER_SLAB, 100);
		FuelRegistry.INSTANCE.add(DRYWALL, 300);
		FuelRegistry.INSTANCE.add(GROWN_RADIO, 300);
		FuelRegistry.INSTANCE.add(BROKEN_RADIO, 300);
		FuelRegistry.INSTANCE.add(WOODEN_RADIO, 300);
		FuelRegistry.INSTANCE.add(TUNED_RADIO, 300);
		FuelRegistry.INSTANCE.add(DEEP_BOOKSHELF, 300);

		StrippableBlockRegistry.register(GAIA_LOG, STRIPPED_GAIA_LOG);
		StrippableBlockRegistry.register(GAIA_WOOD, STRIPPED_GAIA_WOOD);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            entries.accept(STONE_PILLAR);
            entries.accept(DARK_RAILING);
            entries.accept(DRYWALL);
            entries.accept(NYLON_FIBER_BLOCK);
            entries.accept(NYLON_FIBER_STAIRS);
            entries.accept(NYLON_FIBER_SLAB);
            entries.accept(CARVED_GAIA);
            entries
                .addAfter(Items.CHERRY_BUTTON, GAIA_LOG, GAIA_WOOD, STRIPPED_GAIA_LOG, STRIPPED_GAIA_WOOD, GAIA_PLANKS,
                    GAIA_STAIRS, GAIA_SLAB, GAIA_FENCE, GAIA_FENCE_GATE, GAIA_DOOR_ITEM, GAIA_TRAPDOOR,
                    GAIA_PRESSURE_PLATE, GAIA_BUTTON);
        });
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> entries.addAfter(Items.PINK_STAINED_GLASS_PANE, SNOWY_GLASS, SNOWY_GLASS_PANE, SNOWY_GLASS_SLAB));
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
            entries.addAfter(Items.CHISELED_BOOKSHELF, DEEP_BOOKSHELF);
            entries.accept(WOODEN_RADIO);
            entries.accept(TUNED_RADIO);
            entries.accept(BROKEN_RADIO);
            entries.accept(GROWN_RADIO);
        });
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.CHERRY_LOG, GAIA_LOG);
            entries.addAfter(Items.CHERRY_LEAVES, GAIA_LEAVES);
            entries.addAfter(Items.CHERRY_SAPLING, GAIA_SAPLING);
        });
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.CHERRY_HANGING_SIGN, GAIA_SIGN_ITEM, GAIA_HANGING_SIGN_ITEM);
            entries.addAfter(Items.CHISELED_BOOKSHELF, DEEP_BOOKSHELF);
        });
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.addAfter(Items.CHERRY_CHEST_BOAT, GAIA_BOAT, GAIA_CHEST_BOAT));
	}

	private static <B extends Block> B getSingle(String id, B block) {
		return Registry.register(BuiltInRegistries.BLOCK, TheCorners.id(id), block);
	}

	private static <B extends Block> B get(String id, B block) {
		Registry.register(BuiltInRegistries.ITEM, TheCorners.id(id), new BlockItem(block, new FabricItemSettings()));
		return Registry.register(BuiltInRegistries.BLOCK, TheCorners.id(id), block);
	}

	private static <I extends Item> I get(String id, I item) {
		return Registry.register(BuiltInRegistries.ITEM, TheCorners.id(id), item);
	}

}
