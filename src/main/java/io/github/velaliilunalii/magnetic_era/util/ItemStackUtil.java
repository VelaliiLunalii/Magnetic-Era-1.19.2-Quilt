package io.github.velaliilunalii.magnetic_era.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemStackUtil {
	public static void popItemStack(World world, BlockPos pos, Direction direction, Item item){
		popItemStack(world, pos, direction, new ItemStack(item));
	}

	public static void popItemStack(World world, BlockPos pos, Direction direction, ItemStack itemStack){
		itemStack.getOrCreateNbt();
		Vec3d position = Vec3d.of(pos.offset(direction)).add(0.5, 0.5, 0.5);
		world.spawnEntity(new ItemEntity(world,
			position.getX(),
			position.getY(),
			position.getZ(), itemStack,
			direction.equals(Direction.EAST) ? 0.25 : direction.equals(Direction.WEST) ? -0.25 : 0,
			direction.equals(Direction.UP) ? 0.25 : direction.equals(Direction.DOWN) ? -0.25 : 0,
			direction.equals(Direction.SOUTH) ? 0.25 : direction.equals(Direction.NORTH) ? -0.25 : 0));
	}
}
