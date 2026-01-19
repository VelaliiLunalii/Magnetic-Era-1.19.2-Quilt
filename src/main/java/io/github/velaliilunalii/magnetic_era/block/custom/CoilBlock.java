package io.github.velaliilunalii.magnetic_era.block.custom;

import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CoilBlock extends DirectionalBlock{

	public CoilBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient){
			ItemStack itemStack = player.getStackInHand(hand);
			Direction direction = state.get(FACING);
			Block block = state.getBlock();
			if (itemStack.getItem() instanceof ShearsItem){
				if (ModBlocks.COPPER_COIL_MAP.containsKey(block)){
					if (!player.getAbilities().creativeMode) itemStack.damage(1, player, (playerEntityx) -> playerEntityx.sendToolBreakStatus(hand));
					world.setBlockState(pos, ModBlocks.COPPER_COIL_MAP.get(block).getDefaultState().with(FACING, direction), Block.NOTIFY_ALL);
					player.giveItemStack(new ItemStack(ModItems.COPPER_WIRE, 1));
					world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0F, 1.0F);

					return ActionResult.SUCCESS;
				}
				if (ModBlocks.PHASE_COIL_MAP.containsKey(block)){
					if (!player.getAbilities().creativeMode) itemStack.damage(1, player, (playerEntityx) -> playerEntityx.sendToolBreakStatus(hand));
					world.setBlockState(pos, ModBlocks.PHASE_COIL_MAP.get(block).getDefaultState().with(FACING, direction), Block.NOTIFY_ALL);
					player.giveItemStack(new ItemStack(ModItems.PHASE_WIRE, 1));
					world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0F, 1.0F);

					return ActionResult.SUCCESS;
				}
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}
}
