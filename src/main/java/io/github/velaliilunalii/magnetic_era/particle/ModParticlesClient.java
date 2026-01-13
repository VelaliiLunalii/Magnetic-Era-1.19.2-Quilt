package io.github.velaliilunalii.magnetic_era.particle;

import io.github.velaliilunalii.magnetic_era.particle.custom.MagneticBeamParticle;
import io.github.velaliilunalii.magnetic_era.particle.custom.MagneticFieldParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ModParticlesClient {
	public static void register() {
		ParticleFactoryRegistry.getInstance().register(ModParticles.MAGNETIC_FIELD_PARTICLE,
			MagneticFieldParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.MAGNETIC_BEAM_PARTICLE,
			MagneticBeamParticle.Factory::new);
	}
}
