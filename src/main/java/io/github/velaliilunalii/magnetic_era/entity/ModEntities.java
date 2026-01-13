package io.github.velaliilunalii.magnetic_era.entity;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import io.github.velaliilunalii.magnetic_era.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
	public static final EntityType<MagneticFishingRodBobberEntity> MAGNETIC_FISHING_ROD_BOBBER =
		Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "magnetic_fishing_rod_bobber"),
			FabricEntityTypeBuilder.<MagneticFishingRodBobberEntity>create(
					SpawnGroup.MISC, MagneticFishingRodBobberEntity::new)
				.dimensions(EntityDimensions.fixed(0.25F, 0.25F))
				.trackRangeChunks(4).trackedUpdateRate(10)
				.build()
		);

	public static final EntityType<BlockMagneticFieldEntity> BLOCK_MAGNETIC_FIELD =
		Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(MagneticEra.MOD_ID, "block_magnetic_field"),
			FabricEntityTypeBuilder.<BlockMagneticFieldEntity>create(
					SpawnGroup.MISC, BlockMagneticFieldEntity::new)
				.dimensions(EntityDimensions.fixed(0.5f, 0.5f))
				.trackRangeBlocks(64)
				.trackedUpdateRate(1)
				.build()
		);

	public static void register() {
	}
}
