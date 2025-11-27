package io.github.velaliilunalii.vegetarianism.item.custom;

import io.github.velaliilunalii.vegetarianism.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoyPodItem extends Item {
	public SoyPodItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (world instanceof ServerWorld serverWorld) {
			serverWorld.playSound(null, new BlockPos(user.getPos()), SoundEvents.BLOCK_CROP_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
		if (!world.isClient && stack.getItem() instanceof SoyPodItem){
			ItemStack newItem = new ItemStack(ModItems.SOY_BEANS, stack.getCount());
			user.setStackInHand(hand, newItem);
		}
		return super.use(world, user, hand);
	}
}
