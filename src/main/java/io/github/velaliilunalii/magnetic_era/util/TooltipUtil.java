package io.github.velaliilunalii.magnetic_era.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;

public class TooltipUtil {
	public static int magnetizedStyle(@Nullable PlayerEntity player){
//		if (player != null){
//			long time = player.world.getTime();
//			long timeCut = time % 16;
//			if (timeCut >= 14) return 0x01cbae;
//			else if (timeCut >= 12) return 0x2082a6;
//			else if (timeCut >= 10) return 0x524096;
//			else if (timeCut >= 8) return 0x5f2a84;
//			else if (timeCut >= 6) return 0x524096;
//			else if (timeCut >= 4) return 0x2082a6;
//			else if (timeCut >= 2) return 0x01cbae;
//		}
		return 0x01efac;
	}
}
