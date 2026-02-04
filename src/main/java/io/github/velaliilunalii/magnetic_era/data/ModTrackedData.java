package io.github.velaliilunalii.magnetic_era.data;

import io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity.FieldFeatures.TemperatureFeature;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

public class ModTrackedData {
	public static final TrackedDataHandler<TemperatureFeature> TEMPERATURE_FEATURE = TrackedDataHandler.createEnum(TemperatureFeature.class);

	public static void register() {
		TrackedDataHandlerRegistry.register(TEMPERATURE_FEATURE);
	}
}
