package io.github.velaliilunalii.magnetic_era.block.block_entity;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.entity.custom.MagneticFieldEntity;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import io.github.velaliilunalii.magnetic_era.particle.ModParticles;
import io.github.velaliilunalii.magnetic_era.particle.effect.MagneticBeamParticleEffect;
import io.github.velaliilunalii.magnetic_era.sound.ModSounds;
import io.github.velaliilunalii.magnetic_era.util.ItemStackUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.component.NbtComponent;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static io.github.velaliilunalii.magnetic_era.block.custom.MagnetizerBlock.FACING;
import static io.github.velaliilunalii.magnetic_era.block.custom.MagnetizerBlock.POWERED;
import static io.github.velaliilunalii.magnetic_era.entity.custom.MagneticFieldEntity.containsOppositeFields;
import static io.github.velaliilunalii.magnetic_era.entity.custom.MagneticFieldEntity.isMagneticAffected;

public class MagnetizerBlockEntity extends BlockEntity implements  BlockEntityTicker<MagnetizerBlockEntity>, Clearable, SidedInventory {
	private static final int SLOTS = 1;
	private final DefaultedList<ItemStack> itemBeingMagnetized;
	private int magnetizingTime;
	private boolean finishedMagnetizing;

	public boolean hasRecipe(ItemStack stack){
		Item item = stack.getItem();
		return item.equals(Items.IRON_INGOT) || !stack.isStackable()
			|| item.equals(ModBlocks.PHASE_BLOCK.asItem()) || item.equals(ModBlocks.INVERTED_PHASE_BLOCK.asItem());
	}

	public void getItemStack(Direction direction){
		if (world != null) {
			for (int i = 0; i < this.itemBeingMagnetized.size(); ++i) {
				ItemStackUtil.popItemStack(world, pos, direction, this.itemBeingMagnetized.get(i));
				this.itemBeingMagnetized.set(i, ItemStack.EMPTY);
			}
			this.updateListeners();
		}
	}

	public int[] getAvailableSlots(Direction side) {
		return new int[]{0};
	}

	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return isEmpty() && hasRecipe(stack);
	}

	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot == 0 && finishedMagnetizing;	//TODO recipe outputs
	}

	public boolean isEmpty() {
		for(ItemStack itemStack : this.itemBeingMagnetized) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public int size() {
		return SLOTS;
	}

	public ItemStack getStack(int slot) {
		ItemStack result = this.itemBeingMagnetized.get(slot);
		result.getOrCreateNbt();
		return result;
	}

	public ItemStack removeStack(int slot, int amount) {
		ItemStack result = Inventories.splitStack(this.itemBeingMagnetized, slot, amount);
		if (!result.isEmpty()) {
			this.updateListeners();
		}
		result.getOrCreateNbt();
		return result;
	}

	public ItemStack removeStack(int slot) {
		ItemStack result = Inventories.removeStack(this.itemBeingMagnetized, slot);
		if (!result.isEmpty()) {
			this.updateListeners();
		}
		result.getOrCreateNbt();
		return result;
	}

	public int getMaxCountPerStack(){return 1;}

	public void setStack(int slot, ItemStack stack) {
		ItemStack itemStack = (ItemStack)this.itemBeingMagnetized.get(slot);
		boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
		this.itemBeingMagnetized.set(slot, stack);
		if (stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}

		if (slot == 0 && !bl) {
			this.magnetizingTime = 99;	//TODO recipe time
			this.finishedMagnetizing = false;
			this.markDirty();
		}
		updateListeners();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	public MagnetizerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MAGNETIZER_BLOCK_ENTITY, pos, state);
		this.itemBeingMagnetized = DefaultedList.ofSize(SLOTS, ItemStack.EMPTY);
	}

	public void tick(World world, BlockPos pos, BlockState state, MagnetizerBlockEntity blockEntity) {
		if (world == null) return;
		if (!world.isClient) serverTick(world, pos, state, blockEntity);
		clientTick(world, pos, state, blockEntity);

	}

	public static int getMagnetizingTime(MagnetizerBlockEntity magnetizer){return magnetizer.magnetizingTime;}

	public static void serverTick(World world, BlockPos pos, BlockState state, MagnetizerBlockEntity magnetizer) {
		boolean powered = (Boolean)state.get(POWERED);
		if (magnetizer.world.getTime() % 20 == 10) {
			BlockPos startPos = pos.subtract(new Vec3i(25, 25, 25));
			BlockPos endPos = pos.add(new Vec3i(25, 25, 25));
			Box box = new Box(startPos, endPos);
			List<MagneticFieldEntity> entityList = world.getEntitiesByClass(
				MagneticFieldEntity.class,
				box,
				Entity -> isMagneticAffected(Entity, pos)
			);
			boolean oppositeFieldsAffected = containsOppositeFields(entityList);
			if ((!oppositeFieldsAffected && powered) || (oppositeFieldsAffected && !powered)){
				world.setBlockState(pos, (BlockState)state.cycle(POWERED), 2); world.updateNeighborsAlways(pos, state.getBlock());
				Random random = new Random();
				if (powered) world.playSound((PlayerEntity)null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					ModSounds.CAPACITOR_POWERING, SoundCategory.PLAYERS, 1.0F, 0.6f + (random.nextFloat() * 0.2f));
				else world.playSound((PlayerEntity)null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					ModSounds.CAPACITOR_POWERING, SoundCategory.PLAYERS, 1.0F, 1.0f + (random.nextFloat() * 0.2f));
			}
		}

		if(powered){
			boolean hasProcessedItem = false;

			for(int i = 0; i < magnetizer.itemBeingMagnetized.size(); ++i) {
				ItemStack input = (ItemStack)magnetizer.itemBeingMagnetized.get(i);
				if (!input.isEmpty()) {
					hasProcessedItem = true;
					if (magnetizer.magnetizingTime > 0) {
						magnetizer.magnetizingTime --;
					}else {
						if(!magnetizer.finishedMagnetizing){
							ItemStack output = getMagnetizingOutput(input);
							magnetizer.itemBeingMagnetized.set(i, output);
							world.updateListeners(pos, state, state, 3);
							world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.create(state));
							world.playSound((PlayerEntity)null, pos, ModSounds.MAGNETIC_CHIME, SoundCategory.NEUTRAL, 1.0F, 0.8F + (world.getRandom().nextFloat() * 0.4F));
							if (world instanceof ServerWorld serverWorld) {
								Direction.Axis axis = state.get(FACING).getAxis();
								serverWorld.spawnParticles(
									ModParticles.MAGNETIZER_PARTICLE,
									pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
									0,
									axis.equals(Direction.Axis.X) ? 1 : 0,
									axis.equals(Direction.Axis.Y) ? 1 : 0,
									axis.equals(Direction.Axis.Z) ? 1 : 0,
									1
								);
							}
						}
						magnetizer.finishedMagnetizing = true;
					}
				}
			}

			if (hasProcessedItem) {
				markDirty(world, pos, state);
			}
		}
	}

	public static ItemStack getMagnetizingOutput(ItemStack input){
		ItemStack output = input;
		NbtCompound nbtCompound = output.getOrCreateNbt();
		if (input.getItem().equals(Items.IRON_INGOT)) output = new ItemStack(ModItems.PHASE_INGOT, 1);
		else if (input.getItem().equals(ModBlocks.PHASE_BLOCK.asItem())) output = new ItemStack(ModBlocks.INVERTED_PHASE_BLOCK, 1);
		else if (input.getItem().equals(ModBlocks.INVERTED_PHASE_BLOCK.asItem())) output = new ItemStack(ModBlocks.PHASE_BLOCK, 1);
		else if (input.getItem().getMaxCount() == 1) nbtCompound.putBoolean("Magnetized", !nbtCompound.getBoolean("Magnetized"));
		return output;
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, MagnetizerBlockEntity magnetizer) {
		if (world instanceof ServerWorld serverWorld && state.get(POWERED) && !magnetizer.finishedMagnetizing && !magnetizer.isEmpty() && world.getTime() % 10 == 0) {
			Direction direction = state.get(FACING);
			Direction.Axis axis;
			Vec3d startPos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
			boolean clockwise = true;
			if (world.getTime() % 20 == 0) {
				axis = direction.getAxis();
				if (axis.equals(Direction.Axis.X)) startPos = startPos.add((double) 3 / 16, 0.5, 0.5);
				if (axis.equals(Direction.Axis.Y)) startPos = startPos.add(0.5, (double) 3 / 16, 0.5);
				if (axis.equals(Direction.Axis.Z)) startPos = startPos.add(0.5, 0.5, (double) 3 / 16);
			}else {
				direction = direction.getOpposite();
				axis = direction.getAxis();
				startPos = startPos.add(1, 1, 1);
				if (axis.equals(Direction.Axis.X)) startPos = startPos.subtract((double) 3 / 16, 0.5, 0.5);
				if (axis.equals(Direction.Axis.Y)) startPos = startPos.subtract(0.5, (double) 3 / 16, 0.5);
				if (axis.equals(Direction.Axis.Z)) startPos = startPos.subtract(0.5, 0.5, (double) 3 / 16);
				clockwise = false;
			}
			Vec3d directionVector = Vec3d.of(direction.getVector());
			float scale = 0.5f * 0.5f;
			int maxAge = 15;
			MagneticBeamParticleEffect particleEffect = new MagneticBeamParticleEffect(scale, maxAge, clockwise);

			serverWorld.spawnParticles(
				particleEffect,
				startPos.x, startPos.y, startPos.z,
				0,
				directionVector.x / 20,
				directionVector.y / 20,
				directionVector.z / 20,
				1
			);
		}
		if (!world.isClient && state.get(POWERED) && magnetizer.magnetizingTime > 97 && !magnetizer.isEmpty()){
			Random random = new Random();
			world.playSound((PlayerEntity) null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSounds.MAGNETIC_BUZZ, SoundCategory.PLAYERS, 1.0f, 0.9f + (random.nextFloat() * 0.2f));

		}
	}

	public DefaultedList<ItemStack> getItemsBeingMagnetized() {
		for(ItemStack itemStack : itemBeingMagnetized) {
			itemStack.getOrCreateNbt();
		}
		return this.itemBeingMagnetized;
	}

	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.itemBeingMagnetized.clear();
		Inventories.readNbt(nbt, this.itemBeingMagnetized);
		this.magnetizingTime = nbt.getInt("MagnetizingTime");
	}

	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, this.itemBeingMagnetized, true);
		nbt.putInt("MagnetizingTime", this.magnetizingTime);
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}

	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		Inventories.writeNbt(nbtCompound, this.itemBeingMagnetized, true);
		return nbtCompound;
	}

	public boolean addItem(@Nullable Entity user, ItemStack stack) {
		if (hasRecipe(stack)) {
			for (int i = 0; i < this.itemBeingMagnetized.size(); ++i) {
				ItemStack itemStack = (ItemStack) this.itemBeingMagnetized.get(i);
				if (itemStack.isEmpty()) {
					this.finishedMagnetizing = false;
					this.magnetizingTime = 99;	//TODO recipe time
					this.itemBeingMagnetized.set(i, stack.split(1));
					this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Context.create(user, this.getCachedState()));
					this.updateListeners();
					return true;
				}
			}
		}
		return false;
	}

	private void updateListeners() {
		this.markDirty();
		if (this.world != null) {
			this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
		}
	}

	public void clear() {
		this.itemBeingMagnetized.clear();
	}
}
