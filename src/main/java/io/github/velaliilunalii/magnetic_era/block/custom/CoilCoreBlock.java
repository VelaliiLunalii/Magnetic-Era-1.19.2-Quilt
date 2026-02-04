package io.github.velaliilunalii.magnetic_era.block.custom;

import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class CoilCoreBlock extends DirectionalBlock {
	protected static final VoxelShape SHAPE_X = Block.createCuboidShape((double)0.0F, (double)2.0F, (double)2.0F, (double)16.0F, (double)14.0F, (double)14.0F);
	protected static final VoxelShape SHAPE_Y = Block.createCuboidShape((double)2.0F, (double)0.0F, (double)2.0F, (double)14.0F, (double)16.0F, (double)14.0F);
	protected static final VoxelShape SHAPE_Z = Block.createCuboidShape((double)2.0F, (double)2.0F, (double)0.0F, (double)14.0F, (double)14.0F, (double)16.0F);

	public CoilCoreBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction.Axis axis = state.get(FACING).getAxis();
		if (axis.equals(Direction.Axis.X)) return SHAPE_X;
		if (axis.equals(Direction.Axis.Y)) return SHAPE_Y;
		return SHAPE_Z;
	}

	@Override
	public void appendTooltip(ItemStack itemStack, BlockView world, List<Text> tooltip, TooltipContext tooltipContext) {
		String name = itemStack.getItem().toString();
		if (Screen.hasShiftDown()) {
			tooltip.add(Text.translatable("block.magnetic_era." + name + ".tooltip.shift").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("block.magnetic_era.core.tooltip.shift_2").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
		}else {
			tooltip.add(Text.translatable("block.magnetic_era.core.tooltip").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("tooltip.shift").formatted(Formatting.GRAY));
		}
	}
}
