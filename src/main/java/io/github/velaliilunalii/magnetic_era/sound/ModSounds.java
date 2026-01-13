package io.github.velaliilunalii.magnetic_era.sound;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSounds {
	public static final SoundEvent MAGNETIC_BUZZ = registerSoundEvent("magnetic_buzz");

	private static SoundEvent registerSoundEvent(String name) {
		Identifier id = new Identifier(MagneticEra.MOD_ID, name);
		return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
	}

	public static void register() {

	}
}
