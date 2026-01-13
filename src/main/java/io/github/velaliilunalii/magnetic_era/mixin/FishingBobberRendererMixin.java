package io.github.velaliilunalii.magnetic_era.mixin;

import io.github.velaliilunalii.magnetic_era.item.ModItems;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberRendererMixin {

	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
		)
	)
	private boolean redirectRodCheck(ItemStack stack, Item item) {
		return stack.isOf(ModItems.MAGNETIC_FISHING_ROD) || stack.isOf(Items.FISHING_ROD);
	}
}
