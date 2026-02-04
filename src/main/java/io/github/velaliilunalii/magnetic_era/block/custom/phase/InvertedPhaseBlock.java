package io.github.velaliilunalii.magnetic_era.block.custom.phase;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.block_entity.phase.InvertedPhaseBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InvertedPhaseBlock extends BlockWithEntity {
	public static final BooleanProperty POWERED = Properties.OPEN;;

	public InvertedPhaseBlock(Settings settings) {
		super(settings);
		this.setDefaultState((BlockState)this.getDefaultState().with(POWERED, true));
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState)this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{POWERED});
	}

	public boolean emitsRedstonePower(BlockState state) {
		return (Boolean)state.get(POWERED);
	}

	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (Boolean)state.get(POWERED) ? 1 : 0;
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new InvertedPhaseBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.INVERTED_PHASE_BLOCK_ENTITY, (world1, pos, state1, blockEntity)
			-> blockEntity.tick(world1, pos, state1, blockEntity));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void appendTooltip(ItemStack itemStack, BlockView world, List<Text> tooltip, TooltipContext tooltipContext) {
		if (Screen.hasShiftDown()) {
			tooltip.add(Text.translatable("block.magnetic_era.inverted_phase_block.tooltip.shift_1").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("block.magnetic_era.inverted_phase_block.tooltip.shift_2").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
		}else {
			tooltip.add(Text.translatable("block.magnetic_era.phase_block.tooltip_1").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("block.magnetic_era.phase_block.tooltip_2").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("tooltip.shift").formatted(Formatting.GRAY));
		}
	}
}
