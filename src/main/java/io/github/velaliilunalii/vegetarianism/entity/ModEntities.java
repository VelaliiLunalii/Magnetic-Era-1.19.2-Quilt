package io.github.velaliilunalii.vegetarianism.entity;

import io.github.velaliilunalii.vegetarianism.Vegetarianism;
import io.github.velaliilunalii.vegetarianism.entity.custom.MetalDetectorBobberEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
	public static final EntityType<MetalDetectorBobberEntity> METAL_DETECTOR_BOBBER =
		Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(Vegetarianism.MODID, "metal_detector_bobber"),
			FabricEntityTypeBuilder.<MetalDetectorBobberEntity>create(
					SpawnGroup.MISC, MetalDetectorBobberEntity::new)
				.dimensions(EntityDimensions.fixed(0.25F, 0.25F))
				.trackRangeChunks(4).trackedUpdateRate(10)
				.build()
		);

	public static void register() {
	}
}
