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
