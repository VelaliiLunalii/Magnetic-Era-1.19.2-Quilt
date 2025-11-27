package io.github.velaliilunalii.vegetarianism.item.custom;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class MagnetItem extends Item {
	public MagnetItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		user.getItemCooldownManager().set(this, 100);
		if(!world.isClient) {
			double radius = 10.0;
			Box box = new Box(
				user.getX() - radius, user.getY() - radius, user.getZ() - radius,
				user.getX() + radius, user.getY() + radius, user.getZ() + radius
			);

			List<ItemEntity> nearbyItems = user.getWorld().getEntitiesByClass(
				ItemEntity.class,
				box,
				itemEntity -> true // optional filter
			);

			for (ItemEntity item : nearbyItems) {
				double dx = user.getX() - item.getX();
				double dy = user.getY() + 1.0 - item.getY(); // +1 to aim toward head
				double dz = user.getZ() - item.getZ();

				double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

				if (distance > 0.1) { // Avoid division by zero or micro-jitter
					double speed = 2 * (distance / 10); // Adjust as needed
					double vx = (dx / distance) * speed;
					double vy = (dy / distance) * speed;
					double vz = (dz / distance) * speed;

					item.setVelocity(vx, vy, vz);
				}
			}
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}
}
