package io.github.velaliilunalii.magnetic_era.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagneticShieldItem extends ShieldItem {

	public MagneticShieldItem(Settings settings) {
		super(settings);
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.usageTick(world, user, stack, remainingUseTicks);
		if ((getMaxUseTime(stack) - remainingUseTicks) % 5 == 0) {
			Vec3d lookVector = Vec3d.fromPolar(user.getPitch(), user.getYaw()).multiply(2);
			Vec3d fieldPos = user.getEyePos().add(lookVector).subtract(0, 0.5, 0);
//			MagneticFieldEntity magneticFieldEntity = new MagneticFieldEntity(world, user, fieldPos, 0.5F, user.getYaw(), user.getPitch(),
//				5, 3, 3, 3);
//			world.spawnEntity(magneticFieldEntity);
		}
	}
}
