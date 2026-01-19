package io.github.velaliilunalii.magnetic_era.item.custom;

import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static io.github.velaliilunalii.magnetic_era.block.custom.BiAxisBlock.MAIN_AXIS;
import static io.github.velaliilunalii.magnetic_era.block.custom.BiAxisBlock.SECONDARY_AXIS;
import static io.github.velaliilunalii.magnetic_era.block.custom.DirectionalBlock.FACING;

public class PhaseWireItem extends Item {
	public PhaseWireItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!world.isClient){
			PlayerEntity player = context.getPlayer();
			ItemStack itemStack = context.getStack();
			BlockPos pos = context.getBlockPos();
			BlockState state = world.getBlockState(pos);
			Direction surfaceDirection = context.getSide();
			BlockPos pos2 = pos.offset(surfaceDirection);
			Block block = state.getBlock();
			if (itemStack.getItem().equals(ModItems.PHASE_WIRE)) {
				if (ModBlocks.PHASE_CORE_MAP.containsKey(block)) {
					if (!player.getAbilities().creativeMode) itemStack.split(1);
					world.setBlockState(pos, ModBlocks.PHASE_CORE_MAP.get(block).getDefaultState().with(FACING, state.get(FACING)), Block.NOTIFY_ALL);
					world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_COPPER_PLACE, SoundCategory.PLAYERS, 1F, 1F);

					return ActionResult.SUCCESS;
				}
				if (player.canModifyBlocks() && world.isAir(pos2) && world.isSpaceEmpty(new Box(pos2))) {
					if (!player.getAbilities().creativeMode) itemStack.split(1);
					Direction[] directions = Direction.getEntityFacingOrder(player);
					Direction.Axis axis = state.contains(SECONDARY_AXIS) ? state.get(SECONDARY_AXIS) :
						state.contains(FACING) && state.get(FACING).getAxis() != surfaceDirection.getAxis() ? state.get(FACING).getAxis() :
						directions[0].getAxis();
					for (Direction direction : directions) {
						if (surfaceDirection.getAxis() == axis) axis = direction.getAxis();
					}
					world.setBlockState(pos2, ModBlocks.PHASE_WIRE.getDefaultState()
							.with(MAIN_AXIS, surfaceDirection.getAxis())
							.with(SECONDARY_AXIS, axis)
						, Block.NOTIFY_ALL);
					world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_COPPER_PLACE, SoundCategory.PLAYERS, 1F, 1F);

					return ActionResult.SUCCESS;
				}
			}
		}
		return super.useOnBlock(context);
	}
}
