package io.github.velaliilunalii.magnetic_era.mixin;

import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingBobberEntity.class)
public interface FishingBobberEntityAccessorMixin {
	@Accessor("caughtFish")
	boolean getCaughtFish();

	@Accessor("luckOfTheSeaLevel")
	int getLuckOfTheSeaLevel();

	@Mutable
	@Accessor("luckOfTheSeaLevel")
	void setLuckOfTheSeaLevel(int value);

	@Mutable
	@Accessor("lureLevel")
	void setLureLevelLevel(int value);
}
