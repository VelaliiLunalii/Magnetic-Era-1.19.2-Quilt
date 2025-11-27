package io.github.velaliilunalii.vegetarianism.mixin;

import io.github.velaliilunalii.vegetarianism.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FishingBobberEntity.class)
public interface FishingBobberEntityAccessorMixin {

	@Invoker("removeIfInvalid")
	boolean invokeRemoveIfInvalid(PlayerEntity playerEntity);

	@Accessor("hookCountdown")
	int getHookCountdown();

	@Accessor("hookCountdown")
	void setHookCountdown(int value);

	@Accessor("luckOfTheSeaLevel")
	int getLuckOfTheSeaLevel();

	@Mutable
	@Accessor("luckOfTheSeaLevel")
	void setLuckOfTheSeaLevel(int value);

	@Accessor("lureLevel")
	int getLureLevelLevel();

	@Mutable
	@Accessor("lureLevel")
	void setLureLevelLevel(int value);
}
