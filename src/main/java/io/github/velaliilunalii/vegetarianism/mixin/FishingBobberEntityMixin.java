package io.github.velaliilunalii.vegetarianism.mixin;

import io.github.velaliilunalii.vegetarianism.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

	@ModifyVariable(
		method = "removeIfInvalid(Lnet/minecraft/entity/player/PlayerEntity;)Z",
		at = @At("STORE"), // captures the variable right after it's assigned
		ordinal = 0        // ordinal 0 = first boolean local stored → the 'bl' variable
	)
	private boolean modifyMainHandCheck(boolean original, PlayerEntity player) {
		ItemStack main = player.getMainHandStack();
		return main.isOf(ModItems.METAL_DETECTOR_ROD) || main.isOf(Items.FISHING_ROD);
	}

	@ModifyVariable(
		method = "removeIfInvalid(Lnet/minecraft/entity/player/PlayerEntity;)Z",
		at = @At("STORE"),
		ordinal = 1        // second boolean variable stored → 'bl2'
	)
	private boolean modifyOffHandCheck(boolean original, PlayerEntity player) {
		ItemStack off = player.getOffHandStack();
		return off.isOf(ModItems.METAL_DETECTOR_ROD) || off.isOf(Items.FISHING_ROD);
	}

	@ModifyArg(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"
		),
		index = 1
	)
	private double modifyGravity(double originalGravity) {

		FishingBobberEntity self = (FishingBobberEntity)(Object)this;

		PlayerEntity owner = self.getPlayerOwner();
		if (owner == null)
			return originalGravity;

		boolean holdingCustomRod =
			owner.getMainHandStack().isOf(ModItems.METAL_DETECTOR_ROD) ||
				owner.getOffHandStack().isOf(ModItems.METAL_DETECTOR_ROD);

		if (holdingCustomRod) {
			return originalGravity * 3.0;  // double gravity
		}

		return originalGravity;
	}
}
