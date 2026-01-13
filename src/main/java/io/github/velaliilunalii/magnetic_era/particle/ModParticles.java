package io.github.velaliilunalii.magnetic_era.particle;

import com.mojang.serialization.Codec;
import io.github.velaliilunalii.magnetic_era.MagneticEra;
import io.github.velaliilunalii.magnetic_era.particle.effect.MagneticBeamParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModParticles {
	public static final DefaultParticleType MAGNETIC_FIELD_PARTICLE = FabricParticleTypes.simple();
	public static final ParticleType<MagneticBeamParticleEffect> MAGNETIC_BEAM_PARTICLE =
		new ParticleType<MagneticBeamParticleEffect>(false, MagneticBeamParticleEffect.PARAMETERS_FACTORY) {
			@Override
			public Codec<MagneticBeamParticleEffect> getCodec() {
				return null;
			}
		};
	public static void register() {
		Registry.register(Registry.PARTICLE_TYPE, new Identifier(MagneticEra.MOD_ID, "magnetic_field_particle"),
			MAGNETIC_FIELD_PARTICLE);
		Registry.register(Registry.PARTICLE_TYPE, new Identifier(MagneticEra.MOD_ID, "magnetic_beam_particle"),
			MAGNETIC_BEAM_PARTICLE);
	}
}
