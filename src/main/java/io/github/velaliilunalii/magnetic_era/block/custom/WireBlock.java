package io.github.velaliilunalii.magnetic_era.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WireBlock extends Block{
	public static final EnumProperty<Direction.Axis> MAIN_AXIS = EnumProperty.of("main_axis", Direction.Axis.class);
	public static final EnumProperty<Direction.Axis> SECONDARY_AXIS = EnumProperty.of("secondary_axis", Direction.Axis.class);

	protected static final VoxelShape SHAPE_XY = Block.createCuboidShape((double)0.0F, (double)5.0F, (double)7.0F, (double)16.0F, (double)11.0F, (double)9.0F);
	protected static final VoxelShape SHAPE_XZ = Block.createCuboidShape((double)0.0F, (double)7.0F, (double)5.0F, (double)16.0F, (double)9.0F, (double)11.0F);
	protected static final VoxelShape SHAPE_YX = Block.createCuboidShape((double)5.0F, (double)0.0F, (double)7.0F, (double)11.0F, (double)16.0F, (double)9.0F);
	protected static final VoxelShape SHAPE_YZ = Block.createCuboidShape((double)7.0F, (double)0.0F, (double)5.0F, (double)9.0F, (double)16.0F, (double)11.0F);
	protected static final VoxelShape SHAPE_ZX = Block.createCuboidShape((double)5.0F, (double)7.0F, (double)0.0F, (double)11.0F, (double)9.0F, (double)16.0F);
	protected static final VoxelShape SHAPE_ZY = Block.createCuboidShape((double)7.0F, (double)5.0F, (double)0.0F, (double)9.0F, (double)11.0F, (double)16.0F);

	public WireBlock(Settings settings) {
		super(settings);
		this.setDefaultState((BlockState)this.getDefaultState()
			.with(MAIN_AXIS, Direction.Axis.X)
			.with(SECONDARY_AXIS, Direction.Axis.Y));
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction[] directions = Direction.getEntityFacingOrder(ctx.getPlayer());
		return (BlockState)this.getDefaultState()
			.with(MAIN_AXIS, directions[0].getAxis())
			.with(SECONDARY_AXIS, directions[1].getAxis());
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{MAIN_AXIS, SECONDARY_AXIS});
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(player.getStackInHand(hand).isEmpty()) {
			if (!world.isClient) {
				world.setBlockState(pos, state.with(SECONDARY_AXIS, getOtherAxis(state.get(MAIN_AXIS), state.get(SECONDARY_AXIS))), Block.NOTIFY_ALL);
			}else {
				return ActionResult.success(true);
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	public static Direction.Axis getOtherAxis(Direction.Axis axis1, Direction.Axis axis2){
		for (Direction.Axis axis : Direction.Axis.values()){
			if (!axis.equals(axis1) && !axis.equals(axis2)) return axis;
		}
		return null;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction.Axis mainAxis = state.get(MAIN_AXIS);
		Direction.Axis secondaryAxis = state.get(SECONDARY_AXIS);
		if (mainAxis.equals(Direction.Axis.X) && secondaryAxis.equals(Direction.Axis.Y)) return SHAPE_XY;
		if (mainAxis.equals(Direction.Axis.X) && secondaryAxis.equals(Direction.Axis.Z)) return SHAPE_XZ;
		if (mainAxis.equals(Direction.Axis.Y) && secondaryAxis.equals(Direction.Axis.X)) return SHAPE_YX;
		if (mainAxis.equals(Direction.Axis.Y) && secondaryAxis.equals(Direction.Axis.Z)) return SHAPE_YZ;
		if (mainAxis.equals(Direction.Axis.Z) && secondaryAxis.equals(Direction.Axis.X)) return SHAPE_ZX;
		return SHAPE_ZY;
	}
}
