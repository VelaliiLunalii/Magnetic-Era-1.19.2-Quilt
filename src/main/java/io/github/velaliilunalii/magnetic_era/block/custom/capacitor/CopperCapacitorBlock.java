package io.github.velaliilunalii.magnetic_era.block.custom.capacitor;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.block.block_entity.capacitor.CopperCapacitorBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.velaliilunalii.magnetic_era.block.custom.phase.PhaseBlock.POWERED;


public class CopperCapacitorBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.FACING;;
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
	public static final IntProperty CHARGE = Properties.POWER; // 0-15 dans Minecraft, on utilise 0-8
	public static final BooleanProperty ENABLED = Properties.ENABLED;

	private final int maxCharge;

	public CopperCapacitorBlock(Settings settings, int maxCharge) {
		super(settings.ticksRandomly());
		this.maxCharge = maxCharge;
		this.setDefaultState(this.stateManager.getDefaultState()
			.with(FACING, Direction.NORTH)
			.with(LIT, false)
			.with(CHARGE, 0)
			.with(ENABLED, true));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, CHARGE, ENABLED);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState()
			.with(FACING, ctx.getPlayerLookDirection())
			.with(LIT, false)
			.with(CHARGE, 0)
			.with(ENABLED, true);
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

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CopperCapacitorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.COPPER_CAPACITOR_BLOCK_ENTITY,
			(world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	public int getMaxCharge() {
		return maxCharge;
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
