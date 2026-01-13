package io.github.velaliilunalii.magnetic_era.block.custom;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.block_entity.ResonatorBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ResonatorBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.FACING;
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
	public static final BooleanProperty ENABLED = Properties.ENABLED;
	protected static final VoxelShape LIT_SHAPE = Block.createCuboidShape((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)16.0F);
	protected static final VoxelShape UNLIT_SHAPE_X = Block.createCuboidShape((double)1.0F, (double)0.0F, (double)0.0F, (double)15.0F, (double)16.0F, (double)16.0F);
	protected static final VoxelShape UNLIT_SHAPE_Y = Block.createCuboidShape((double)0.0F, (double)1.0F, (double)0.0F, (double)16.0F, (double)15.0F, (double)16.0F);
	protected static final VoxelShape UNLIT_SHAPE_Z = Block.createCuboidShape((double)0.0F, (double)0.0F, (double)1.0F, (double)16.0F, (double)16.0F, (double)15.0F);

	public ResonatorBlock(Settings settings) {
		super(settings);
		this.setDefaultState((BlockState)((BlockState)((BlockState)this.getDefaultState())
			.with(FACING, Direction.NORTH))
			.with(LIT, false)
			.with(ENABLED, true));
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState)this.getDefaultState()
			.with(FACING, ctx.getPlayerLookDirection().getOpposite())
			.with(LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()))
			.with(ENABLED, true);
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING, LIT, ENABLED});
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player.isSneaking()) {
			if (state.get(ENABLED))
				world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.NEUTRAL, 1F, 0.8F);
			if (!state.get(ENABLED))
				world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.NEUTRAL, 1F, 1.2F);
			if (world.isClient) {
				return ActionResult.success(true);
			} else {
				world.setBlockState(pos, (BlockState) state.cycle(ENABLED), 2);
				return ActionResult.CONSUME;
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction.Axis axis = state.get(FACING).getAxis();
		if (!state.get(LIT)){
			if (axis.equals(Direction.Axis.X)) return UNLIT_SHAPE_X;
			if (axis.equals(Direction.Axis.Y)) return UNLIT_SHAPE_Y;
			return UNLIT_SHAPE_Z;
		}
		return LIT_SHAPE;
	}

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
	}

	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		if ((Boolean)state.get(LIT) && !world.isReceivingRedstonePower(pos)) {
			world.setBlockState(pos, (BlockState)state.cycle(LIT), 2);
		}
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ResonatorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.RESONATOR_BLOCK_ENTITY, (world1, pos, state1, blockEntity)
			-> blockEntity.tick(world1, pos, state1, blockEntity));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
