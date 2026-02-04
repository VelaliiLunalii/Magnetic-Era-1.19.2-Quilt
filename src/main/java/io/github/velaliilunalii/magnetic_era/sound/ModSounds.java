package io.github.velaliilunalii.magnetic_era.sound;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSounds {
	public static final SoundEvent MAGNETIC_BUZZ = registerSoundEvent("magnetic_buzz");
	public static final SoundEvent MECHANICAL_PUSH = registerSoundEvent("mechanical_push");
	public static final SoundEvent CAPACITOR_POWERING = registerSoundEvent("capacitor_powering");
	public static final SoundEvent MAGNETIC_CHIME = registerSoundEvent("magnetic_chime");

	private static SoundEvent registerSoundEvent(String name) {
		Identifier id = new Identifier(MagneticEra.MOD_ID, name);
		return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
	}

	public static void register() {

	}
}
