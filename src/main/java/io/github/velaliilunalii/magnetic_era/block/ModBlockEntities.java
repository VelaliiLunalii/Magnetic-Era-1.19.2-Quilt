package io.github.velaliilunalii.magnetic_era.block;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import io.github.velaliilunalii.magnetic_era.block.block_entity.MagnetizerBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.capacitor.*;
import io.github.velaliilunalii.magnetic_era.block.block_entity.phase.InvertedPhaseBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.phase.PhaseBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.ResonatorBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {

	public static final BlockEntityType<CopperCapacitorBlockEntity> COPPER_CAPACITOR_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "copper_capacitor"),
			BlockEntityType.Builder.create(
				CopperCapacitorBlockEntity::new,
				ModBlocks.COPPER_CAPACITOR
			).build(null)
		);

	public static final BlockEntityType<IronCapacitorBlockEntity> IRON_CAPACITOR_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "iron_capacitor"),
			BlockEntityType.Builder.create(
				IronCapacitorBlockEntity::new,
				ModBlocks.IRON_CAPACITOR
			).build(null)
		);

	public static final BlockEntityType<NetheriteCapacitorBlockEntity> NETHERITE_CAPACITOR_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "netherite_capacitor"),
			BlockEntityType.Builder.create(
				NetheriteCapacitorBlockEntity::new,
				ModBlocks.NETHERITE_CAPACITOR
			).build(null)
		);

	public static final BlockEntityType<CalibratedCopperCapacitorBlockEntity> CALIBRATED_COPPER_CAPACITOR_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "calibrated_copper_capacitor"),
			BlockEntityType.Builder.create(
				CalibratedCopperCapacitorBlockEntity::new,
				ModBlocks.CALIBRATED_COPPER_CAPACITOR
			).build(null)
		);

	public static final BlockEntityType<CalibratedIronCapacitorBlockEntity> CALIBRATED_IRON_CAPACITOR_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "calibrated_iron_capacitor"),
			BlockEntityType.Builder.create(
				CalibratedIronCapacitorBlockEntity::new,
				ModBlocks.CALIBRATED_IRON_CAPACITOR
			).build(null)
		);

	public static final BlockEntityType<CalibratedNetheriteCapacitorBlockEntity> CALIBRATED_NETHERITE_CAPACITOR_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "calibrated_netherite_capacitor"),
			BlockEntityType.Builder.create(
				CalibratedNetheriteCapacitorBlockEntity::new,
				ModBlocks.CALIBRATED_NETHERITE_CAPACITOR
			).build(null)
		);

	public static final BlockEntityType<PhaseBlockEntity> PHASE_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "phase_block"),
			BlockEntityType.Builder.create(
				PhaseBlockEntity::new,
				ModBlocks.PHASE_BLOCK
			).build(null)
		);

	public static final BlockEntityType<InvertedPhaseBlockEntity> INVERTED_PHASE_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "inverted_phase_block"),
			BlockEntityType.Builder.create(
				InvertedPhaseBlockEntity::new,
				ModBlocks.INVERTED_PHASE_BLOCK
			).build(null)
		);

	public static final BlockEntityType<ResonatorBlockEntity> RESONATOR_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "resonator"),
			BlockEntityType.Builder.create(
				ResonatorBlockEntity::new,
				ModBlocks.RESONATOR
			).build(null)
		);

	public static final BlockEntityType<MagnetizerBlockEntity> MAGNETIZER_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "magnetizer"),
			BlockEntityType.Builder.create(
				MagnetizerBlockEntity::new,
				ModBlocks.MAGNETIZER
			).build(null)
		);

	public static void register() {
	}
}
