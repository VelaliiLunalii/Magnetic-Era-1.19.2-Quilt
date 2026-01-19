package io.github.velaliilunalii.magnetic_era.block.custom;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.block.block_entity.CapacitorBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.MagnetizerBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.capacitor.CopperCapacitorBlockEntity;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class CapacitorBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.FACING;
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
	public static final IntProperty LEVEL = IntProperty.of("level", 0, 8);		//configurable redstone amount
	public static final IntProperty CHARGE = IntProperty.of("charge", 0, 8);	//powered levels
	public static final BooleanProperty ENABLED = Properties.ENABLED;

	public CapacitorBlock(Settings settings) {
		super(settings.ticksRandomly());
		this.setDefaultState(this.stateManager.getDefaultState()
			.with(FACING, Direction.NORTH)
			.with(LIT, false)
			.with(LEVEL, 0)
			.with(CHARGE, 0)
			.with(ENABLED, true));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, LEVEL, CHARGE, ENABLED);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState()
			.with(FACING, ctx.getPlayer().isSneaking() ?
				ctx.getPlayerLookDirection() : ctx.getPlayerLookDirection().getOpposite())
			.with(LIT, false)
			.with(LEVEL, 0)
			.with(CHARGE, 0)
			.with(ENABLED, true);
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
				} else {
					return ActionResult.success(true);
				}
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		System.out.println("hi");
		super.afterBreak(world, player, pos, state, blockEntity, stack);
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
}
