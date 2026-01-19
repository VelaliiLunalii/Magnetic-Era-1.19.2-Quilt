package io.github.velaliilunalii.magnetic_era.block.block_entity.capacitor;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.block.custom.CoilBlock;
import io.github.velaliilunalii.magnetic_era.block.custom.capacitor.CopperCapacitorBlock;
import io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity;
import io.github.velaliilunalii.magnetic_era.entity.custom.CalibratedBlockMagneticFieldEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

import static io.github.velaliilunalii.magnetic_era.block.custom.capacitor.CopperCapacitorBlock.*;
import static io.github.velaliilunalii.magnetic_era.block.custom.capacitor.CopperCapacitorBlock.LIT;
import static io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity.FieldType.CALIBRATED_CAPACITOR;
import static io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity.FieldType.CAPACITOR;

public class CopperCapacitorBlockEntity extends BlockEntity implements BlockEntityTicker<CopperCapacitorBlockEntity> {
	private int charge;
	private int checkCooldown = 0;
	private int maxCharge = 1;

	public CopperCapacitorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.COPPER_CAPACITOR_BLOCK_ENTITY, pos, state);
		if (state.getBlock() instanceof CopperCapacitorBlock capacitor) {
			this.maxCharge = capacitor.getMaxCharge();
		}
	}

	@Override
	public void tick(World world, BlockPos pos, BlockState state, CopperCapacitorBlockEntity blockEntity) {
		if (world == null || world.isClient) return;

		updateCharge(state);

		if (checkCooldown > 0) {
			checkCooldown--;
		} else if (state.get(LIT)) {
			checkForCoilRing(world, pos, state);
		}
	}

	/**
	 * Gestion de la charge et mise à jour du blockstate
	 */
	private void updateCharge(BlockState state) {
		boolean receivingPower = world.isReceivingRedstonePower(pos);
		boolean isLit = state.get(LIT);

		// Augmenter/diminuer la charge
		if (receivingPower && charge < maxCharge) {
			charge = Math.min(charge + 1, maxCharge);
			markDirty();
		} else if (!receivingPower && charge > 0) {
			charge--;
		}

		// Calculer le niveau de charge (0-8)
		int chargeLevel = getChargeLevel();
		boolean shouldBeLit = charge >= maxCharge;
		if (isLit != shouldBeLit || state.get(CHARGE) != chargeLevel) {
			world.setBlockState(pos, state
				.with(CHARGE, chargeLevel), Block.NOTIFY_ALL);
		}

		if (!isLit && charge >= maxCharge) world.setBlockState(pos, (BlockState)state.cycle(LIT), 2);
		if (isLit && charge <= 0) world.setBlockState(pos, (BlockState)state.cycle(LIT), 2);
	}

	/**
	 * Retourne le niveau de charge de 0 à 8
	 */
	public int getChargeLevel() {
		if (maxCharge == 0) return 0;
		return Math.round((charge * 8.0f) / maxCharge);
	}

	/**
	 * Vérifie si un anneau de coils est complet
	 */
	private void checkForCoilRing(World world, BlockPos pos, BlockState state) {
		BlockMagneticFieldEntity field = isCoilComplete(world, pos, state.get(FACING));
		if (field != null) {
			if (!state.get(ENABLED)) field.disableParticles();
			world.spawnEntity(field);
			checkCooldown = 100;
		} else {
			checkCooldown = 20;
		}
	}

	public static BlockMagneticFieldEntity isCoilComplete(World world, BlockPos startingPos, Direction direction){
		BlockMagneticFieldEntity magneticFieldEntity;
		BlockPos runningPos = startingPos.offset(direction);
		Direction endDirection = checkPerpendicular(world, startingPos, direction);
		int edgeCount = 0;
		BlockState[] cornerList = new BlockState[3];
		ArrayList<Block> weakCoilList = new ArrayList<>();
		ArrayList<Block> strongCoilList = new ArrayList<>();
		if (endDirection != null) {
			endDirection = endDirection.getOpposite();
			Direction runningOffset = direction;
			for (int i = 0; i < 4; ++i) {
				int newEdgeCount = 0;
				Block coil = world.getBlockState(runningPos).getBlock();
				while (coil instanceof CoilBlock){
					newEdgeCount++;
					if (coil.equals(ModBlocks.WEAK_COPPER_COIL)) weakCoilList.add(coil);
					if (coil.equals(ModBlocks.STRONG_COPPER_COIL)) strongCoilList.add(coil);
					runningPos = runningPos.offset(runningOffset);
					coil = world.getBlockState(runningPos).getBlock();
				}

				if (i == 0){
					edgeCount = newEdgeCount;
				}else{
					if (newEdgeCount != edgeCount) return null;
				}
				if (i != 3){
					BlockState blockState = world.getBlockState(runningPos);
					if(blockState.getBlock() instanceof CopperCapacitorBlock && blockState.get(LIT)) return null;
					cornerList[i] = blockState;
				}

				if (i == 0) runningOffset = endDirection.getOpposite();
				if (i == 1) runningOffset = direction.getOpposite();
				if (i == 2) runningOffset = endDirection;
				if (i != 3) runningPos = runningPos.offset(runningOffset);
			}
		}
		if(runningPos.equals(startingPos)) {
			Vec3d fieldPos = new Vec3d(startingPos.getX(), startingPos.getY(), startingPos.getZ())
				.add(0.5, 0.25, 0.5)
				.relative(direction, (double) (edgeCount + 1) /2)
				.relative(endDirection.getOpposite(), (double) (edgeCount + 1) /2);
			Direction orthogonal = getOrthogonal(direction, endDirection);

			int lengthIncrease = 0;
			for (BlockState blockState : cornerList){
				Block block = blockState.getBlock();
				if (isExtenderCornerBlock(block)) lengthIncrease += 2;
			}
			int directionLength = 1 + lengthIncrease;

			int coilAmount = (edgeCount * 4);

			float weakCoilRatio = (float) weakCoilList.size() /coilAmount;
			float direction_multiplicator = ((1 - weakCoilRatio) * 0.4F) + 0.1F;

			float strongCoilRatio = (float) strongCoilList.size() /coilAmount;
			float centerStrength = strongCoilRatio * 0.2F;

			magneticFieldEntity = new BlockMagneticFieldEntity(world, fieldPos, direction_multiplicator, orthogonal, directionLength, edgeCount, centerStrength, coilAmount, CAPACITOR.getTypeId());
			return magneticFieldEntity;
		}
		return null;

	}

	public static BlockMagneticFieldEntity isCalibratedCoilComplete(World world, BlockPos startingPos, Direction direction){
		BlockMagneticFieldEntity magneticFieldEntity;
		BlockPos runningPos = startingPos.offset(direction);
		Direction endDirection = checkPerpendicular(world, startingPos, direction);
		int edgeCount = 0;
		BlockState[] cornerList = new BlockState[3];
		ArrayList<Block> weakCoilList = new ArrayList<>();
		ArrayList<Block> strongCoilList = new ArrayList<>();
		if (endDirection != null) {
			endDirection = endDirection.getOpposite();
			Direction runningOffset = direction;
			for (int i = 0; i < 4; ++i) {
				int newEdgeCount = 0;
				Block coil = world.getBlockState(runningPos).getBlock();
				while (coil instanceof CoilBlock){
					newEdgeCount++;
					if (coil.equals(ModBlocks.WEAK_COPPER_COIL)) weakCoilList.add(coil);
					if (coil.equals(ModBlocks.STRONG_COPPER_COIL)) strongCoilList.add(coil);
					runningPos = runningPos.offset(runningOffset);
					coil = world.getBlockState(runningPos).getBlock();
				}

				if (i == 0){
					edgeCount = newEdgeCount;
				}else{
					if (newEdgeCount != edgeCount) return null;
				}
				if (i != 3){
					BlockState blockState = world.getBlockState(runningPos);
					if(blockState.getBlock() instanceof CopperCapacitorBlock && blockState.get(LIT)) return null;
					cornerList[i] = blockState;
				}

				if (i == 0) runningOffset = endDirection.getOpposite();
				if (i == 1) runningOffset = direction.getOpposite();
				if (i == 2) runningOffset = endDirection;
				if (i != 3) runningPos = runningPos.offset(runningOffset);
			}
		}
		if(runningPos.equals(startingPos)) {
			Vec3d fieldPos = new Vec3d(startingPos.getX(), startingPos.getY(), startingPos.getZ())
				.add(0.5, 0.25, 0.5)
				.relative(direction, (double) (edgeCount + 1) /2)
				.relative(endDirection.getOpposite(), (double) (edgeCount + 1) /2);
			Direction orthogonal = getOrthogonal(direction, endDirection);

			int lengthIncrease = 0;
			for (BlockState blockState : cornerList){
				Block block = blockState.getBlock();
				if (isExtenderCornerBlock(block)) lengthIncrease += 2;
			}
			int directionLength = 1 + lengthIncrease;

			int coilAmount = (edgeCount * 4);

			float weakCoilRatio = (float) weakCoilList.size() /coilAmount;
			float direction_multiplicator = ((1 - weakCoilRatio) * 0.4F) + 0.1F;

			float strongCoilRatio = (float) strongCoilList.size() /coilAmount;
			float centerStrength = strongCoilRatio * 0.2F;

			magneticFieldEntity = new CalibratedBlockMagneticFieldEntity(world, fieldPos, direction_multiplicator,
				orthogonal, directionLength, edgeCount, centerStrength, coilAmount, CALIBRATED_CAPACITOR.getTypeId());
			return magneticFieldEntity;
		}
		return null;

	}

	public static boolean isExtenderCornerBlock(Block block){
		return block.equals(Blocks.COPPER_BLOCK) || block.equals(Blocks.WAXED_COPPER_BLOCK) ||
			block.equals(Blocks.EXPOSED_COPPER) || block.equals(Blocks.WAXED_EXPOSED_COPPER) ||
			block.equals(Blocks.WEATHERED_COPPER) || block.equals(Blocks.WAXED_WEATHERED_COPPER) ||
			block.equals(Blocks.OXIDIZED_COPPER) || block.equals(Blocks.WAXED_OXIDIZED_COPPER) ||
			block.equals(Blocks.IRON_BLOCK) || block.equals(Blocks.NETHERITE_BLOCK);
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

	public static Direction checkPerpendicular(World world, BlockPos pos, Direction originalDirection){
		for (Direction direction : Direction.values()){
			if (direction != originalDirection && direction != originalDirection.getOpposite()){
				Block block = world.getBlockState(pos.offset(direction)).getBlock();
				if(block == ModBlocks.COPPER_COIL || block == ModBlocks.WEAK_COPPER_COIL || block == ModBlocks.STRONG_COPPER_COIL) return direction;
			}
		}
		return null;
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
}
