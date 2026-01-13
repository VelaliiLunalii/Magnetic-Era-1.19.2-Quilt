package io.github.velaliilunalii.magnetic_era.block.custom;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.block_entity.MagnetizerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MagnetizerBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;
	protected static final VoxelShape SHAPE_X = Block.createCuboidShape((double)0.0F, (double)2.0F, (double)2.0F, (double)16.0F, (double)14.0F, (double)14.0F);
	protected static final VoxelShape SHAPE_Y = Block.createCuboidShape((double)2.0F, (double)0.0F, (double)2.0F, (double)14.0F, (double)16.0F, (double)14.0F);
	protected static final VoxelShape SHAPE_Z = Block.createCuboidShape((double)2.0F, (double)2.0F, (double)0.0F, (double)14.0F, (double)14.0F, (double)16.0F);

	public MagnetizerBlock(Settings settings) {
		super(settings);
		this.setDefaultState((BlockState)this.getDefaultState()
			.with(FACING, Direction.NORTH)
			.with(POWERED, false));
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState)this.getDefaultState()
			.with(FACING, ctx.getPlayerLookDirection().getOpposite())
			.with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING, POWERED});
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction.Axis axis = state.get(FACING).getAxis();
		if (axis.equals(Direction.Axis.X)) return SHAPE_X;
		if (axis.equals(Direction.Axis.Y)) return SHAPE_Y;
		return SHAPE_Z;
	}

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MagnetizerBlockEntity magnetizerBlockEntity) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (!world.isClient) {
				if (!magnetizerBlockEntity.isEmpty()) {
					magnetizerBlockEntity.getItemStack(player);
				}
				if (magnetizerBlockEntity.addItem(player, player.getAbilities().creativeMode ? itemStack.copy() : itemStack)) {
//				player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
				}
				return ActionResult.SUCCESS;
			}
			return ActionResult.CONSUME;
		}


//			Optional<CampfireCookingRecipe> optional = magnetizerBlockEntity.getRecipeFor(itemStack);
//			if (optional.isPresent()) {
//				if (!world.isClient && magnetizerBlockEntity.addItem(player, player.getAbilities().creativeMode ? itemStack.copy() : itemStack, ((CampfireCookingRecipe)optional.get()).getCookTime())) {
//					player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
//					return ActionResult.SUCCESS;
//				}
//
//				return ActionResult.CONSUME;
//			}
//		}

		return ActionResult.PASS;
	}

	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof MagnetizerBlockEntity) {
				ItemScatterer.spawn(world, pos, ((MagnetizerBlockEntity)blockEntity).getItemsBeingMagnetized());
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		if ((Boolean)state.get(POWERED)) {

		}
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MagnetizerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.MAGNETIZER_BLOCK_ENTITY,
			(world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
	}


	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
