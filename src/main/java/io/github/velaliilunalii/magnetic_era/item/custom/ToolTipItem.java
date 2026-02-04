package io.github.velaliilunalii.magnetic_era.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class ToolTipItem extends Item {
	public ToolTipItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
		String name = itemStack.getItem().toString();
		if (Screen.hasShiftDown()) {
			tooltip.add(Text.translatable("item.magnetic_era." + name + ".tooltip.shift").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
		}else{
			tooltip.add(Text.translatable("item.magnetic_era." + name + ".tooltip").styled(style -> style.withItalic(true)).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("tooltip.shift").formatted(Formatting.GRAY));
		}
	}
}
