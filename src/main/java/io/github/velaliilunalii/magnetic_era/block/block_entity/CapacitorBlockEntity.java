package io.github.velaliilunalii.magnetic_era.block.block_entity;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.custom.CoilBlock;
import io.github.velaliilunalii.magnetic_era.block.custom.WireBlock;
import io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity;
import io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity.FieldFeatures.TemperatureFeature;
import io.github.velaliilunalii.magnetic_era.particle.ModParticles;
import io.github.velaliilunalii.magnetic_era.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static io.github.velaliilunalii.magnetic_era.block.custom.CapacitorBlock.*;

public class CapacitorBlockEntity extends BlockEntity implements BlockEntityTicker<CapacitorBlockEntity> {
	private int charge;
	private int checkCooldown = 0;
	private final int maxCharge = 160;

	public CapacitorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CAPACITOR_BLOCK_ENTITY, pos, state);
	}

	@Override
	public void tick(World world, BlockPos pos, BlockState state, CapacitorBlockEntity blockEntity) {
		if (world == null || world.isClient) return;

		updateCharge(state);

		if (checkCooldown > 0) {
			checkCooldown--;
		} else if (state.get(LIT)) {
			BlockMagneticFieldEntity field = isCoilComplete(world, pos, state.get(FACING), state.get(LEVEL) * 2);
			if (field != null) {
				if (!state.get(ENABLED)) field.disableParticles();
				world.spawnEntity(field);
				checkCooldown = 92;
			} else {
				checkCooldown = 20;
			}
		}
	}

	private void updateCharge(BlockState state) {
		boolean receivingPower = world.isReceivingRedstonePower(pos);
		boolean isLit = state.get(LIT);
		int level = state.get(LEVEL);
		int levelMaxCharge = Math.round(maxCharge * ((float)level/8));

		if (receivingPower && charge < levelMaxCharge && level > 0) {
			charge = Math.min(charge + 1, levelMaxCharge);
			markDirty();
		} else if (!receivingPower && charge > 0) {
			charge--;
		}

		int chargeLevel = Math.round((float) charge / 20);
		boolean shouldBeLit = charge >= levelMaxCharge;
		if (state.get(CHARGE) != chargeLevel) {
			world.setBlockState(pos, state
				.with(CHARGE, chargeLevel), Block.NOTIFY_ALL);
			if (chargeLevel > 0) world.playSound((PlayerEntity)null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				ModSounds.CAPACITOR_POWERING, SoundCategory.PLAYERS, 1.0F, (0.6f + (chargeLevel * 0.1f)));
		}

		if (!isLit && shouldBeLit && level > 0) world.setBlockState(pos, (BlockState)state.cycle(LIT), 2);
		if (isLit && charge <= 0) world.setBlockState(pos, (BlockState)state.cycle(LIT), 2);
	}

	public static Block getBlock(World world, BlockPos pos){return world.getBlockState(pos).getBlock();}

	public static BlockMagneticFieldEntity isCoilComplete(World world, BlockPos pos, Direction direction, int level){
		Direction originalDirection1 = null;
		Direction originalDirection2 = null;
		BlockPos runningPos = pos.offset(direction);
		BlockPos firstCornerPos = null;
		int edgeCount = 0;
		BlockState[] cornerList = new BlockState[4];
		int[] fieldFeatures = {0, 0};
		TemperatureFeature temperatureFeature = TemperatureFeature.TEMPERATE;
		boolean calibrated = false;

		int distance = 0;
		while (distance < 32 && isWireBlock(getBlock(world, runningPos))){
			if (getBlock(world, runningPos).equals(Blocks.AMETHYST_BLOCK)) calibrated = true;
			else if (getBlock(world, runningPos).equals(Blocks.MAGMA_BLOCK)) temperatureFeature = TemperatureFeature.HOT;
			else if (getBlock(world, runningPos).equals(Blocks.POWDER_SNOW)) temperatureFeature = TemperatureFeature.COLD;
			runningPos = runningPos.offset(direction);
			distance ++;
		}
		BlockPos startPos = runningPos;

		if(getBlock(world, runningPos) instanceof CoilBlock){
			Direction runningDirection = world.getBlockState(runningPos).get(FACING);
			originalDirection1 = runningDirection;

			for (int i = 0; i < 5; ++i){
				int newEdgeCount = 0;
				Block coil = getBlock(world, runningPos);
				while (coil instanceof CoilBlock && newEdgeCount < level){
					if (i == 4 && runningPos.equals(startPos)) break;
					newEdgeCount++;

					CoilBlock.CoilFeatures coilFeatures = CoilBlock.CoilFeatures.getCoilFeatures(world, runningPos);
					fieldFeatures[coilFeatures.pullsToCenter() ? 1 : 0] += coilFeatures.getPullStrength();

					runningPos = runningPos.offset(runningDirection);
					coil = world.getBlockState(runningPos).getBlock();
				}

				//first corner direction test
				if (i == 0){
					for (Direction cornerDirection : Direction.values()){
						if (cornerDirection.getAxis() != originalDirection1.getAxis()
							&& getBlock(world, runningPos.offset(cornerDirection)) instanceof CoilBlock) {
							originalDirection2 = cornerDirection;
							firstCornerPos = runningPos;
							break;
						}
					}
					if(originalDirection2 == null) return null;
				}

				//edge length test
				if (i == 1){
					edgeCount = newEdgeCount;
				}else if (i > 1 && i < 4){
					if (newEdgeCount != edgeCount) return null;
				}

				if (i == 0) runningDirection = originalDirection2;
				if (i == 1) runningDirection = originalDirection1.getOpposite();
				if (i == 2) runningDirection = originalDirection2.getOpposite();
				if (i == 3) runningDirection = originalDirection1;

				if (i != 4) {
					//corner test
					BlockState blockState = world.getBlockState(runningPos);
					cornerList[i] = blockState;

					runningPos = runningPos.offset(runningDirection);
				}
			}
		}

		if(edgeCount > 0 && runningPos.equals(startPos)) {
			return getMagneticField(world, firstCornerPos, originalDirection1, originalDirection2, edgeCount, fieldFeatures, cornerList, temperatureFeature, calibrated);
		}
		return null;
	}

	public static BlockMagneticFieldEntity getMagneticField(World world, BlockPos firstCornerPos, Direction originalDirection1, Direction originalDirection2, int edgeCount, int[] fieldFeatures, BlockState[] cornerList, TemperatureFeature temperatureFeature, boolean calibrated){
		Vec3d fieldPos = new Vec3d(firstCornerPos.getX(), firstCornerPos.getY(), firstCornerPos.getZ())
			.add(0.5, 0.25, 0.5)
			.relative(originalDirection1.getOpposite(), (double) (edgeCount + 1) /2)
			.relative(originalDirection2, (double) (edgeCount + 1) /2);

		if (hasField(world, fieldPos)) return null;

		int lengthIncrease = 0;
		for (BlockState blockState : cornerList){
			Block block = blockState.getBlock();
			if (isExtenderCornerBlock(block)) lengthIncrease += 2;
		}

		int coilAmount = (edgeCount * 4);

		return new BlockMagneticFieldEntity(world, fieldPos,
			(float) fieldFeatures[0] / (10 * coilAmount),
			getOrthogonal(originalDirection1.getOpposite(), originalDirection2),
			1 + lengthIncrease,
			edgeCount,
			(float) fieldFeatures[1] / (20 * coilAmount),
			coilAmount,
			false,
			temperatureFeature,
			calibrated);
	}

	public static boolean hasField(World world, Vec3d fieldPos){
		Box box = new Box(new BlockPos(fieldPos));
		List<Entity> entityList = world.getEntitiesByClass(
			Entity.class,
			box,
			entity -> entity instanceof BlockMagneticFieldEntity blockMagneticFieldEntity && blockMagneticFieldEntity.getAgeProgress() < 0.9
		);
		return !entityList.isEmpty();
	}

	public static boolean isExtenderCornerBlock(Block block){
		return block.equals(Blocks.COPPER_BLOCK) || block.equals(Blocks.WAXED_COPPER_BLOCK) ||
			block.equals(Blocks.EXPOSED_COPPER) || block.equals(Blocks.WAXED_EXPOSED_COPPER) ||
			block.equals(Blocks.WEATHERED_COPPER) || block.equals(Blocks.WAXED_WEATHERED_COPPER) ||
			block.equals(Blocks.OXIDIZED_COPPER) || block.equals(Blocks.WAXED_OXIDIZED_COPPER) ||
			block.equals(Blocks.IRON_BLOCK) || block.equals(Blocks.NETHERITE_BLOCK);
	}

	public static boolean isWireBlock(Block block){
		return block instanceof WireBlock || block.equals(Blocks.AMETHYST_BLOCK) || block.equals(Blocks.MAGMA_BLOCK)
			|| block.equals(Blocks.POWDER_SNOW);
	}

	//SE, WS, NW, EN : D	XZ
	//ED, UE, WU, DW : S	XY
	//SD, US, NU, DN : W	ZY
	public static Direction getOrthogonal(Direction direction1, Direction direction2){
		Direction.Axis axis1 = direction1.getAxis();
		Direction.Axis axis2 = direction2.getAxis();
		Direction orthogonal = null;

		if ((axis1 == Direction.Axis.X && axis2 == Direction.Axis.Y) || (axis1 == Direction.Axis.Y && axis2 == Direction.Axis.X)){
			orthogonal = ((direction1.equals(Direction.EAST) && direction2.equals(Direction.DOWN)) ||
				(direction1.equals(Direction.UP) && direction2.equals(Direction.EAST)) ||
				(direction1.equals(Direction.WEST) && direction2.equals(Direction.UP)) ||
				(direction1.equals(Direction.DOWN) && direction2.equals(Direction.WEST))) ? Direction.SOUTH : Direction.NORTH;
		}
		if ((axis1 == Direction.Axis.X && axis2 == Direction.Axis.Z) || (axis1 == Direction.Axis.Z && axis2 == Direction.Axis.X)){
			orthogonal = ((direction1.equals(Direction.SOUTH) && direction2.equals(Direction.EAST)) ||
				(direction1.equals(Direction.WEST) && direction2.equals(Direction.SOUTH)) ||
				(direction1.equals(Direction.NORTH) && direction2.equals(Direction.WEST)) ||
				(direction1.equals(Direction.EAST) && direction2.equals(Direction.NORTH))) ? Direction.DOWN : Direction.UP;
		}
		if ((axis1 == Direction.Axis.Y && axis2 == Direction.Axis.Z) || (axis1 == Direction.Axis.Z && axis2 == Direction.Axis.Y)){
			orthogonal = ((direction1.equals(Direction.SOUTH) && direction2.equals(Direction.DOWN)) ||
				(direction1.equals(Direction.UP) && direction2.equals(Direction.SOUTH)) ||
				(direction1.equals(Direction.NORTH) && direction2.equals(Direction.UP)) ||
				(direction1.equals(Direction.DOWN) && direction2.equals(Direction.NORTH))) ? Direction.WEST : Direction.EAST;
		}
		return orthogonal;
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("Charge", this.charge);
		nbt.putInt("CheckCooldown", this.checkCooldown);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.charge = nbt.getInt("Charge");
		this.checkCooldown = nbt.getInt("CheckCooldown");
	}

	//----------------------get-----------------------------

	public void resetCharge() {
		charge = 0;
	}
}
