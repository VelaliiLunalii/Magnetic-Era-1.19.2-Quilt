package io.github.velaliilunalii.magnetic_era.block;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import io.github.velaliilunalii.magnetic_era.block.block_entity.CapacitorBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.MagnetizerBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.phase.InvertedPhaseBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.phase.PhaseBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.ResonatorBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {

	public static final BlockEntityType<CapacitorBlockEntity> CAPACITOR_BLOCK_ENTITY =
		Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "capacitor"),
			BlockEntityType.Builder.create(
				CapacitorBlockEntity::new,
				ModBlocks.CAPACITOR
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
