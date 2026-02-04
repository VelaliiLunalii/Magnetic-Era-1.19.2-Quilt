package io.github.velaliilunalii.magnetic_era.block.custom;

import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import io.github.velaliilunalii.magnetic_era.particle.ModParticles;
import io.github.velaliilunalii.magnetic_era.util.ItemStackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class CoilBlock extends DirectionalBlock{
	public enum CoilFeatures{
		WEAK(false, 1),
		NORMAL(false, 3),
		STRONG(false, 5),
		CENTER_WEAK(true, 1),
		CENTER_NORMAL(true, 3),
		CENTER_STRONG(true, 5);

		private final boolean centerPull;
		private final int pullStrength;

		CoilFeatures(boolean centerPull, int pullStrength) {
			this.centerPull = centerPull;
			this.pullStrength = pullStrength;
		}

		public boolean pullsToCenter() {
			return centerPull;
		}
		public int getPullStrength() {
			return pullStrength;
		}

		public static CoilFeatures getCoilFeatures(World world, BlockPos pos){
			Block block = world.getBlockState(pos).getBlock();
			if (block.equals(ModBlocks.WEAK_COPPER_COIL)) return WEAK;
			if (block.equals(ModBlocks.COPPER_COIL)) return NORMAL;
			if (block.equals(ModBlocks.STRONG_COPPER_COIL)) return STRONG;
			if (block.equals(ModBlocks.WEAK_PHASE_COIL)) return CENTER_WEAK;
			if (block.equals(ModBlocks.PHASE_COIL)) return CENTER_NORMAL;
			if (block.equals(ModBlocks.STRONG_PHASE_COIL)) return CENTER_STRONG;
			System.out.println(pos);
			return NORMAL;
		}
	}

	public CoilBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		Direction direction = state.get(FACING);
		Direction hitDirection = hit.getSide();
		ItemStack itemStack = player.getStackInHand(hand);

		if(player.getStackInHand(hand).isEmpty()) {
			if (!world.isClient) {
				if (player.isSneaking()) {
					direction = direction.getOpposite();
					world.setBlockState(pos, state.with(FACING, direction), Block.NOTIFY_ALL);
				}
				if (world instanceof ServerWorld serverWorld) {
					Vec3d startPos = Vec3d.of(pos.offset(hitDirection))
						.add(0.5, 0.5, 0.5)
						.relative(hitDirection.getOpposite(), 0.4)
						.relative(direction.getOpposite(), 0.5);
					serverWorld.spawnParticles(ModParticles.MAGNETIC_FIELD_PARTICLE,
						startPos.x, startPos.y, startPos.z, 0,
						direction.equals(Direction.EAST) ? 1 : direction.equals(Direction.WEST) ? -1 : 0,
						direction.equals(Direction.UP) ? 1 : direction.equals(Direction.DOWN) ? -1 : 0,
						direction.equals(Direction.SOUTH) ? 1 : direction.equals(Direction.NORTH) ? -1 : 0,
						0.03);
				}
			}else {
				return ActionResult.success(true);
			}
		}
		else if(itemStack.getItem() instanceof ShearsItem) {
			if (!world.isClient) {
				Block block = state.getBlock();
				if (ModBlocks.COPPER_COIL_MAP.containsKey(block)){
					if (!player.getAbilities().creativeMode) itemStack.damage(1, player, (playerEntityx) -> playerEntityx.sendToolBreakStatus(hand));
					world.setBlockState(pos, ModBlocks.COPPER_COIL_MAP.get(block).getDefaultState().with(FACING, direction), Block.NOTIFY_ALL);
					ItemStackUtil.popItemStack(world, pos, hitDirection, ModItems.COPPER_WIRE);
					world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0F, 1.0F);

					return ActionResult.SUCCESS;
				}
				if (ModBlocks.PHASE_COIL_MAP.containsKey(block)){
					if (!player.getAbilities().creativeMode) itemStack.damage(1, player, (playerEntityx) -> playerEntityx.sendToolBreakStatus(hand));
					world.setBlockState(pos, ModBlocks.PHASE_COIL_MAP.get(block).getDefaultState().with(FACING, direction), Block.NOTIFY_ALL);
					ItemStackUtil.popItemStack(world, pos, hitDirection, ModItems.PHASE_WIRE);
					world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0F, 1.0F);

					return ActionResult.SUCCESS;
				}
			}else {
				return ActionResult.success(true);
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, BlockView world, List<Text> tooltip, TooltipContext tooltipContext) {
		String name = itemStack.getItem().toString();
		if (Screen.hasShiftDown()) {
			tooltip.add(Text.translatable("block.magnetic_era." + name + ".tooltip.shift").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("block.magnetic_era.coil.tooltip.shift_1").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("block.magnetic_era.coil.tooltip.shift_2").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
		}else {
			tooltip.add(Text.translatable("block.magnetic_era.coil.tooltip").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("tooltip.shift").formatted(Formatting.GRAY));
		}
	}
}
