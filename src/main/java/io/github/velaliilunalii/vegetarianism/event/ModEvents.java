package io.github.velaliilunalii.vegetarianism.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.ModContainer;

public class ModEvents {
	public static void register(ModContainer mod) {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.isClient) return ActionResult.PASS;

			if (!player.isSneaking()) return ActionResult.PASS;

			BlockPos pos = hitResult.getBlockPos();
			BlockState state = world.getBlockState(pos);
			if (!state.isOf(Blocks.GRINDSTONE)) return ActionResult.PASS;

			ItemStack held = player.getStackInHand(hand);
			if (!held.isIn(ItemTags.COALS)) return ActionResult.PASS;

			held.decrement(1);

			ItemStack reward = new ItemStack(Items.BLACK_DYE);
			if (!player.getInventory().insertStack(reward)) {
				player.dropItem(reward, false);
			}

			world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE,
				SoundCategory.BLOCKS, 1.0f, 1.0f);

			return ActionResult.PASS;
		});
	}
}
