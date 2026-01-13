package io.github.velaliilunalii.magnetic_era.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class BiAxisBlock extends Block {
	public static final EnumProperty<Direction.Axis> MAIN_AXIS = EnumProperty.of("main_axis", Direction.Axis.class);
	public static final EnumProperty<Direction.Axis> SECONDARY_AXIS = EnumProperty.of("secondary_axis", Direction.Axis.class);
	protected static final VoxelShape SHAPE_XY = Block.createCuboidShape((double)0.0F, (double)5.0F, (double)7.0F, (double)16.0F, (double)11.0F, (double)9.0F);
	protected static final VoxelShape SHAPE_XZ = Block.createCuboidShape((double)0.0F, (double)7.0F, (double)5.0F, (double)16.0F, (double)9.0F, (double)11.0F);
	protected static final VoxelShape SHAPE_YX = Block.createCuboidShape((double)5.0F, (double)0.0F, (double)7.0F, (double)11.0F, (double)16.0F, (double)9.0F);	// z pas bcp x bcp
	protected static final VoxelShape SHAPE_YZ = Block.createCuboidShape((double)7.0F, (double)0.0F, (double)5.0F, (double)9.0F, (double)16.0F, (double)11.0F);
	protected static final VoxelShape SHAPE_ZX = Block.createCuboidShape((double)5.0F, (double)7.0F, (double)0.0F, (double)11.0F, (double)9.0F, (double)16.0F);	//X pas bcp y bcp
	protected static final VoxelShape SHAPE_ZY = Block.createCuboidShape((double)7.0F, (double)5.0F, (double)0.0F, (double)9.0F, (double)11.0F, (double)16.0F);

	public BiAxisBlock(Settings settings) {
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
