package io.github.velaliilunalii.magnetic_era.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

import static io.github.velaliilunalii.magnetic_era.util.TooltipUtil.magnetizedStyle;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(method = "inventoryTick", at = @At("HEAD"))
	private void inventoryTickInjected(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
		ItemStack itemStack = (ItemStack) (Object) this;
		if (!world.isClient && world.getTime() % 5 == 0 && entity instanceof PlayerEntity player){
			boolean isHeldOrEquipped = player.getMainHandStack().equals(itemStack) || player.getOffHandStack().equals(itemStack);
			if (!isHeldOrEquipped) {
				for (ItemStack armorStack : player.getArmorItems()) {
					if (armorStack.equals(itemStack)) {
						isHeldOrEquipped = true;
						break;
					}
				}
			}
			if (isHeldOrEquipped && itemStack.getOrCreateNbt().getBoolean("Magnetized")) {
				double radius = 10.0;
				Box box = new Box(
					player.getX() - radius, player.getY() - radius, player.getZ() - radius,
					player.getX() + radius, player.getY() + radius, player.getZ() + radius
				);

				List<ItemEntity> nearbyItems = player.getWorld().getEntitiesByClass(
					ItemEntity.class,
					box,
					itemEntity -> true
				);

				for (ItemEntity item : nearbyItems) {
					double dx = player.getX() - item.getX();
					double dy = player.getY() + 1.0 - item.getY();
					double dz = player.getZ() - item.getZ();

					double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

					if (distance > 0.1) {
						double speed = distance / (10 - (distance / 2));
						double vx = (dx / distance) * speed;
						double vy = (dy / distance) * speed;
						double vz = (dz / distance) * speed;

						item.setVelocity(vx, vy, vz);
					}
				}
			}
		}
	}

	@Inject(method = "getTooltip", at = @At("TAIL"), cancellable = true)
	private void tooltipInjected(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir){
		ItemStack itemStack = (ItemStack) (Object) this;
		List<Text> list = cir.getReturnValue();
		if (itemStack.hasNbt()) {
			if (itemStack.getNbt().getBoolean("Magnetized")) {
				list.add(Text.literal("Magnetized").styled(style -> style.withColor(magnetizedStyle(player))));
			}
		}

		cir.setReturnValue(list);
	}
}

























