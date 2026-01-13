package io.github.velaliilunalii.magnetic_era.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class AxisBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

	public AxisBlock(Settings settings) {
		super(settings);
		this.setDefaultState((BlockState)this.getDefaultState()
			.with(AXIS, Direction.Axis.Y));
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState)this.getDefaultState()
			.with(AXIS, ctx.getPlayerLookDirection().getAxis());
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{AXIS});
	}
}
