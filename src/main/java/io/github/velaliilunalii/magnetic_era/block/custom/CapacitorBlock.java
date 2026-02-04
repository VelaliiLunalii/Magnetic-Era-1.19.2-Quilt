package io.github.velaliilunalii.magnetic_era.block.custom;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.block_entity.CapacitorBlockEntity;
import io.github.velaliilunalii.magnetic_era.sound.ModSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static io.github.velaliilunalii.magnetic_era.block.custom.DirectionalBlock.FACING;
import static io.github.velaliilunalii.magnetic_era.block.custom.WireBlock.*;


public class CapacitorBlock extends BlockWithEntity {

	protected static final double[] COORDINATES_UP = {(double) 0.0F, (double) 0.0F, (double) 0.0F, (double) 16.0F, (double) 5.0F, (double) 16.0F};
	protected static final double[] COORDINATES_DOWN = {(double)0.0F, (double)11.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)16.0F};
	protected static final double[] COORDINATES_SOUTH = {(double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)5.0F};
	protected static final double[] COORDINATES_NORTH = {(double)0.0F, (double)0.0F, (double)11.0F, (double)16.0F, (double)16.0F, (double)16.0F};
	protected static final double[] COORDINATES_EAST = {(double)0.0F, (double)0.0F, (double)0.0F, (double)5.0F, (double)16.0F, (double)16.0F};
	protected static final double[] COORDINATES_WEST = {(double)11.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)16.0F};

	protected static final VoxelShape[] SHAPES_UP = composeShape(COORDINATES_UP, Direction.UP);
	protected static final VoxelShape[] SHAPES_DOWN = composeShape(COORDINATES_DOWN, Direction.DOWN);
	protected static final VoxelShape[] SHAPES_SOUTH = composeShape(COORDINATES_SOUTH, Direction.SOUTH);
	protected static final VoxelShape[] SHAPES_NORTH = composeShape(COORDINATES_NORTH, Direction.NORTH);
	protected static final VoxelShape[] SHAPES_EAST = composeShape(COORDINATES_EAST, Direction.EAST);
	protected static final VoxelShape[] SHAPES_WEST = composeShape(COORDINATES_WEST, Direction.WEST);

	public static VoxelShape[] composeShape(double[] coordinates, Direction direction){
		VoxelShape[] shapes = new VoxelShape[9];
		double xMin = direction.equals(Direction.WEST) ? -1 : 0;
		double yMin = direction.equals(Direction.DOWN) ? -1 : 0;
		double zMin = direction.equals(Direction.NORTH) ? -1 : 0;
		double xMax = direction.equals(Direction.EAST) ? 1 : 0;
		double yMax = direction.equals(Direction.UP) ? 1 : 0;
		double zMax = direction.equals(Direction.SOUTH) ? 1 : 0;

		for (int i = 0; i < 9; i++) {
			VoxelShape shape = Block.createCuboidShape(
				coordinates[0] + xMin * i,
				coordinates[1] + yMin * i,
				coordinates[2] + zMin * i,
				coordinates[3] + xMax * i,
				coordinates[4] + yMax * i,
				coordinates[5] + zMax * i);
			shapes[i] = shape;
		}

		return shapes;
	}

	public static final DirectionProperty FACING = Properties.FACING;
	public static final EnumProperty<Direction.Axis> SECONDARY_AXIS = EnumProperty.of("secondary_axis", Direction.Axis.class);
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
	public static final IntProperty LEVEL = IntProperty.of("level", 0, 8);		//configurable redstone amount
	public static final IntProperty CHARGE = IntProperty.of("charge", 0, 8);	//powered levels
	public static final BooleanProperty ENABLED = Properties.ENABLED;

	public CapacitorBlock(Settings settings) {
		super(settings.ticksRandomly());
		this.setDefaultState(this.stateManager.getDefaultState()
			.with(FACING, Direction.NORTH)
			.with(SECONDARY_AXIS, Direction.Axis.Y)
			.with(LIT, false)
			.with(LEVEL, 0)
			.with(CHARGE, 0)
			.with(ENABLED, true));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, SECONDARY_AXIS, LIT, LEVEL, CHARGE, ENABLED);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction[] directions = Direction.getEntityFacingOrder(ctx.getPlayer());
		Direction direction = ctx.getPlayerLookDirection();
		World world = ctx.getWorld();
		BlockState state = world.getBlockState(ctx.getBlockPos().offset(direction));
		return this.getDefaultState()
			.with(FACING, direction)
			.with(SECONDARY_AXIS, state.contains(SECONDARY_AXIS) ? state.get(SECONDARY_AXIS) :
				state.contains(FACING) && state.get(FACING).getAxis() != direction.getAxis() ? state.get(FACING).getAxis() :
					ctx.getPlayer().isSneaking() ? directions[1].getAxis() : directions[2].getAxis())
			.with(LIT, false)
			.with(LEVEL, 0)
			.with(CHARGE, 0)
			.with(ENABLED, true);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (fromPos.equals(pos.offset(state.get(FACING)))) {
			BlockState blockState = world.getBlockState(fromPos);
			if (blockState.contains(SECONDARY_AXIS) && !blockState.get(SECONDARY_AXIS).equals(state.get(SECONDARY_AXIS))
				&& !blockState.get(SECONDARY_AXIS).equals(state.get(FACING).getAxis()))
				world.setBlockState(pos, state.with(SECONDARY_AXIS, blockState.get(SECONDARY_AXIS)));
			else if (blockState.contains(FACING) && blockState.get(FACING).getAxis() != state.get(FACING).getAxis())
				world.setBlockState(pos, state.with(SECONDARY_AXIS, blockState.get(FACING).getAxis()));
		}
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(player.getStackInHand(hand).isEmpty()) {
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
			} else {
				if (!world.isClient) {
					if (state.get(LEVEL) < 8) {
						world.setBlockState(pos, state.with(LEVEL, state.get(LEVEL) + 1), Block.NOTIFY_ALL);
					} else {
						world.setBlockState(pos, state.with(LEVEL, 0).with(CHARGE, 0), Block.NOTIFY_ALL);
						if (world.getBlockEntity(pos) instanceof CapacitorBlockEntity capacitorBlockEntity) {
							capacitorBlockEntity.resetCharge();
						}
					}
					Random random = new Random();
					world.playSound((PlayerEntity)null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSounds.MECHANICAL_PUSH, SoundCategory.PLAYERS, 1.0F, 0.9f + (random.nextFloat() * 0.2f));
				} else {
					return ActionResult.success(true);
				}
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CapacitorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.CAPACITOR_BLOCK_ENTITY,
			(world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		if ((Boolean)state.get(LIT) && world.getTime() % 3 == 0) {
			double x = pos.getX() - 0.1;
			double y = pos.getY() - 0.1;
			double z = pos.getZ() - 0.1;
			world.addParticle(DustParticleEffect.DEFAULT, x+ random.nextDouble()*1.2, y+ random.nextDouble()*1.2, z, 0, 0, 0);
			world.addParticle(DustParticleEffect.DEFAULT, x+ random.nextDouble()*1.2, y+ random.nextDouble()*1.2, z + 1.2, 0, 0, 0);
			world.addParticle(DustParticleEffect.DEFAULT, x, y+ random.nextDouble()*1.2, z+ random.nextDouble()*1.2, 0, 0, 0);
			world.addParticle(DustParticleEffect.DEFAULT, x + 1.2, y+ random.nextDouble()*1.2, z+ random.nextDouble()*1.2, 0, 0, 0);
			world.addParticle(DustParticleEffect.DEFAULT, x+ random.nextDouble()*1.2, y, z+ random.nextDouble()*1.2, 0, 0, 0);
			world.addParticle(DustParticleEffect.DEFAULT, x+ random.nextDouble()*1.2, y + 1.2, z+ random.nextDouble()*1.2, 0, 0, 0);
		}
	}

	public static VoxelShape getLevelShape(int level, Direction direction){
		if (direction.equals(Direction.UP)) return SHAPES_UP[level];
		if (direction.equals(Direction.DOWN)) return SHAPES_DOWN[level];
		if (direction.equals(Direction.NORTH)) return SHAPES_NORTH[level];
		if (direction.equals(Direction.SOUTH)) return SHAPES_SOUTH[level];
		if (direction.equals(Direction.EAST)) return SHAPES_EAST[level];
		return SHAPES_WEST[level];
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int level = state.get(LEVEL);
		Direction mainDirection = state.get(FACING);
		Direction.Axis secondaryAxis = state.get(SECONDARY_AXIS);
		if (mainDirection.equals(Direction.EAST) && secondaryAxis.equals(Direction.Axis.Y)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_XY);
		if (mainDirection.equals(Direction.EAST) && secondaryAxis.equals(Direction.Axis.Z)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_XZ);
		if (mainDirection.equals(Direction.UP) && secondaryAxis.equals(Direction.Axis.X)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_YX);
		if (mainDirection.equals(Direction.UP) && secondaryAxis.equals(Direction.Axis.Z)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_YZ);
		if (mainDirection.equals(Direction.NORTH) && secondaryAxis.equals(Direction.Axis.X)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_ZX);
		if (mainDirection.equals(Direction.NORTH) && secondaryAxis.equals(Direction.Axis.Y)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_ZY);

		if (mainDirection.equals(Direction.WEST) && secondaryAxis.equals(Direction.Axis.Y)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_XY);
		if (mainDirection.equals(Direction.WEST) && secondaryAxis.equals(Direction.Axis.Z)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_XZ);
		if (mainDirection.equals(Direction.DOWN) && secondaryAxis.equals(Direction.Axis.X)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_YX);
		if (mainDirection.equals(Direction.DOWN) && secondaryAxis.equals(Direction.Axis.Z)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_YZ);
		if (mainDirection.equals(Direction.SOUTH) && secondaryAxis.equals(Direction.Axis.X)) return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_ZX);
		return VoxelShapes.union(getLevelShape(level, mainDirection), SHAPE_ZY);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, BlockView world, List<Text> tooltip, TooltipContext tooltipContext) {
		if (Screen.hasShiftDown()) {
			tooltip.add(Text.translatable("block.magnetic_era.capacitor.tooltip.shift_1").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("block.magnetic_era.capacitor.tooltip.shift_2").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("block.magnetic_era.capacitor.tooltip.shift_3").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
		}else {
			tooltip.add(Text.translatable("block.magnetic_era.capacitor.tooltip").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("tooltip.shift").formatted(Formatting.GRAY));
		}
	}
}
