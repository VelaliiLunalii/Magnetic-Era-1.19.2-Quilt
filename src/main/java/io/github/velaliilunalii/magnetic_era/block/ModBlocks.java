package io.github.velaliilunalii.magnetic_era.block;

import io.github.velaliilunalii.magnetic_era.block.custom.*;
import io.github.velaliilunalii.magnetic_era.block.custom.capacitor.*;
import io.github.velaliilunalii.magnetic_era.block.custom.crate.IronCrateBlock;
import io.github.velaliilunalii.magnetic_era.block.custom.crate.MossyCrateBlock;
import io.github.velaliilunalii.magnetic_era.block.custom.crate.WeightedCompanionCrateClass;
import io.github.velaliilunalii.magnetic_era.block.custom.crate.WoodenCrateBlock;
import io.github.velaliilunalii.magnetic_era.block.custom.phase.InvertedPhaseBlock;
import io.github.velaliilunalii.magnetic_era.block.custom.phase.PhaseBlock;
import io.github.velaliilunalii.magnetic_era.item.ModItemGroups;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ToIntFunction;

public class ModBlocks {
	public enum WoodVariants{
		SPRUCE("spruce"),
		OAK("oak"),
		ACACIA("acacia"),
		BIRCH("birch"),
		CRIMSON("crimson"),
		DARK_OAK("dark_oak"),
		JUNGLE("jungle"),
		MANGROVE("mangrove"),
		WARPED("warped");

		private final String name;

		WoodVariants(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static final Map<WoodVariants, Block> WOODEN_CRATES = new HashMap<>();
	public static final Map<WoodVariants, Block> MOSSY_CRATES = new HashMap<>();

	private static void wooden_and_mossy_crates_maker(ModContainer mod){
		for (WoodVariants wood_variant : WoodVariants.values()){
			Block block = new WoodenCrateBlock(QuiltBlockSettings.copyOf(Blocks.OAK_PLANKS));
			String name = wood_variant.getName().concat("_crate");

			Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), name), block);
			Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), name),
				new BlockItem(block, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));

			WOODEN_CRATES.put(wood_variant, block);

			block = new MossyCrateBlock(QuiltBlockSettings.copyOf(Blocks.OAK_PLANKS));

			Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "mossy_".concat(name)), block);
			Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "mossy_".concat(name)),
				new BlockItem(block, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));

			MOSSY_CRATES.put(wood_variant, block);
		}
	}

	public static final Block IRON_CRATE = new IronCrateBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK));
	public static final Block WEIGHTED_COMPANION_CRATE = new WeightedCompanionCrateClass(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK));

	public static final Block COPPER_CAPACITOR = new CopperCapacitorBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK), 320);
	public static final Block IRON_CAPACITOR = new IronCapacitorBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK), 16);
	public static final Block NETHERITE_CAPACITOR = new NetheriteCapacitorBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK), 320);
	public static final Block CALIBRATED_COPPER_CAPACITOR = new CalibratedCopperCapacitorBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK), 320);
	public static final Block CALIBRATED_IRON_CAPACITOR = new CalibratedIronCapacitorBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK), 16);
	public static final Block CALIBRATED_NETHERITE_CAPACITOR = new CalibratedNetheriteCapacitorBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK), 320);
	public static final Block MAGNETIC_COIL = new CoilBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());
	public static final Block WEAK_MAGNETIC_COIL = new CoilBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());
	public static final Block STRONG_MAGNETIC_COIL = new CoilBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());
	public static final Block MAGNETIC_CORE = new CoilCoreBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());
	public static final Block WEAK_MAGNETIC_CORE = new CoilCoreBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());
	public static final Block STRONG_MAGNETIC_CORE = new CoilCoreBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());
	public static final Block PHASE_BLOCK = new PhaseBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(createLightLevelFromBoolean(PhaseBlock.POWERED, 5)));
	public static final Block INVERTED_PHASE_BLOCK = new InvertedPhaseBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(createLightLevelFromBoolean(InvertedPhaseBlock.POWERED, 5)));
	public static final Block RESONATOR = new ResonatorBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().luminance(createLightLevelFromBoolean(ResonatorBlock.LIT, 8)));
	public static final Block MAGNETIZER = new MagnetizerBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().luminance(createLightLevelFromBoolean(MagnetizerBlock.POWERED, 8)));
	public static final Block COPPER_WIRE_BLOCK = new AxisBlock(QuiltBlockSettings.copyOf(Blocks.COPPER_BLOCK));
	public static final Block COPPER_WIRE = new WireBlock(QuiltBlockSettings.copyOf(Blocks.COPPER_BLOCK).nonOpaque().allowsSpawning(ModBlocks::never).solidBlock(ModBlocks::never).suffocates(ModBlocks::never).blockVision(ModBlocks::never));

	public static final Map<Block, Block> COIL_MAP = Map.of(
		MAGNETIC_COIL, MAGNETIC_CORE, WEAK_MAGNETIC_COIL, WEAK_MAGNETIC_CORE, STRONG_MAGNETIC_COIL, STRONG_MAGNETIC_CORE);
	public static final Map<Block, Block> CORE_MAP = Map.of(
		MAGNETIC_CORE, MAGNETIC_COIL, WEAK_MAGNETIC_CORE, WEAK_MAGNETIC_COIL, STRONG_MAGNETIC_CORE, STRONG_MAGNETIC_COIL);

	private static ToIntFunction<BlockState> createLightLevelFromBoolean(BooleanProperty property, int litLevel) {
		return (state) -> (Boolean)state.get(property) ? litLevel : 0;
	}

	private static Boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return false;
	}
	private static boolean never(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	public static void register(ModContainer mod) {
		wooden_and_mossy_crates_maker(mod);

		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "iron_crate"), IRON_CRATE);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "iron_crate"),
			new BlockItem(IRON_CRATE, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "weighted_companion_crate"), WEIGHTED_COMPANION_CRATE);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "weighted_companion_crate"),
			new BlockItem(WEIGHTED_COMPANION_CRATE, new QuiltItemSettings()));

		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "copper_capacitor"), COPPER_CAPACITOR);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "copper_capacitor"),
			new BlockItem(COPPER_CAPACITOR, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "iron_capacitor"), IRON_CAPACITOR);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "iron_capacitor"),
			new BlockItem(IRON_CAPACITOR, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "netherite_capacitor"), NETHERITE_CAPACITOR);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "netherite_capacitor"),
			new BlockItem(NETHERITE_CAPACITOR, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "calibrated_copper_capacitor"), CALIBRATED_COPPER_CAPACITOR);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "calibrated_copper_capacitor"),
			new BlockItem(CALIBRATED_COPPER_CAPACITOR, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "calibrated_iron_capacitor"), CALIBRATED_IRON_CAPACITOR);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "calibrated_iron_capacitor"),
			new BlockItem(CALIBRATED_IRON_CAPACITOR, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "calibrated_netherite_capacitor"), CALIBRATED_NETHERITE_CAPACITOR);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "calibrated_netherite_capacitor"),
			new BlockItem(CALIBRATED_NETHERITE_CAPACITOR, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "magnetic_coil"), MAGNETIC_COIL);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnetic_coil"),
			new BlockItem(MAGNETIC_COIL, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "weak_magnetic_coil"), WEAK_MAGNETIC_COIL);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "weak_magnetic_coil"),
			new BlockItem(WEAK_MAGNETIC_COIL, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "strong_magnetic_coil"), STRONG_MAGNETIC_COIL);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "strong_magnetic_coil"),
			new BlockItem(STRONG_MAGNETIC_COIL, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "magnetic_core"), MAGNETIC_CORE);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnetic_core"),
			new BlockItem(MAGNETIC_CORE, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "weak_magnetic_core"), WEAK_MAGNETIC_CORE);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "weak_magnetic_core"),
			new BlockItem(WEAK_MAGNETIC_CORE, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "strong_magnetic_core"), STRONG_MAGNETIC_CORE);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "strong_magnetic_core"),
			new BlockItem(STRONG_MAGNETIC_CORE, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));

		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "phase_block"), PHASE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "phase_block"),
			new BlockItem(PHASE_BLOCK, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "inverted_phase_block"), INVERTED_PHASE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "inverted_phase_block"),
			new BlockItem(INVERTED_PHASE_BLOCK, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "resonator"), RESONATOR);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "resonator"),
			new BlockItem(RESONATOR, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "magnetizer"), MAGNETIZER);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnetizer"),
			new BlockItem(MAGNETIZER, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));

		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "copper_wire_block"), COPPER_WIRE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "copper_wire_block"),
			new BlockItem(COPPER_WIRE_BLOCK, new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "copper_wire"), COPPER_WIRE);
	}
}
