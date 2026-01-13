package io.github.velaliilunalii.magnetic_era.block.block_entity.capacitor;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.custom.capacitor.NetheriteCapacitorBlock;
import io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static io.github.velaliilunalii.magnetic_era.block.block_entity.capacitor.CopperCapacitorBlockEntity.isCoilComplete;
import static io.github.velaliilunalii.magnetic_era.block.custom.capacitor.CopperCapacitorBlock.*;

public class NetheriteCapacitorBlockEntity extends BlockEntity implements BlockEntityTicker<NetheriteCapacitorBlockEntity> {
	private int charge;
	private int checkCooldown = 0;
	private int maxCharge = 1;

	public NetheriteCapacitorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.NETHERITE_CAPACITOR_BLOCK_ENTITY, pos, state);
		if (state.getBlock() instanceof NetheriteCapacitorBlock capacitor) {
			this.maxCharge = capacitor.getMaxCharge();
		}
	}

	@Override
	public void tick(World world, BlockPos pos, BlockState state, NetheriteCapacitorBlockEntity blockEntity) {
		if (world == null || world.isClient) return;

		updateCharge(state);

		if (checkCooldown > 0) {
			checkCooldown--;
		} else if (state.get(LIT)) {
			checkForCoilRing(world, pos, state);
		}
	}

	private void updateCharge(BlockState state) {
		boolean receivingPower = world.isReceivingRedstonePower(pos);
		boolean isLit = state.get(LIT);

		if (receivingPower && charge < maxCharge) {
			charge = Math.min(charge + 1, maxCharge);
			markDirty();
		} else if (!receivingPower && charge > 0) {
			charge--;
		}

		int chargeLevel = getChargeLevel();
		boolean shouldBeLit = charge >= maxCharge;
		if (isLit != shouldBeLit || state.get(CHARGE) != chargeLevel) {
			world.setBlockState(pos, state
				.with(CHARGE, chargeLevel), Block.NOTIFY_ALL);
		}

		if (!isLit && charge >= maxCharge) world.setBlockState(pos, (BlockState)state.cycle(LIT), 2);
	}

	public int getChargeLevel() {
		if (maxCharge == 0) return 0;
		return Math.round((charge * 8.0f) / maxCharge);
	}

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
