package io.github.velaliilunalii.magnetic_era.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.velaliilunalii.magnetic_era.particle.ModParticles;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class MagneticBeamParticleEffect implements ParticleEffect {
	private final float scale;
	private final int maxAge;
	private final boolean clockwise;

	public static final ParticleEffect.Factory<MagneticBeamParticleEffect> PARAMETERS_FACTORY =
		new ParticleEffect.Factory<MagneticBeamParticleEffect>() {
			@Override
			public MagneticBeamParticleEffect read(ParticleType<MagneticBeamParticleEffect> type, StringReader reader)
				throws CommandSyntaxException {
				reader.expect(' ');
				float scale = reader.readFloat();
				reader.expect(' ');
				int maxAge = reader.readInt();
				reader.expect(' ');
				boolean clockwise = reader.readBoolean();
				return new MagneticBeamParticleEffect(scale, maxAge, clockwise);
			}

			@Override
			public MagneticBeamParticleEffect read(ParticleType<MagneticBeamParticleEffect> type, PacketByteBuf buf) {
				return new MagneticBeamParticleEffect(buf.readFloat(), buf.readInt(), buf.readBoolean());
			}
		};

	public MagneticBeamParticleEffect(float scale, int maxAge, boolean clockwise) {
		this.scale = scale;
		this.maxAge = maxAge;
		this.clockwise = clockwise;
	}

	@Override
	public ParticleType<?> getType() {
		return ModParticles.MAGNETIC_BEAM_PARTICLE;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.scale);
		buf.writeInt(this.maxAge);
		buf.writeBoolean(this.clockwise);
	}

	@Override
	public String asString() {
		return String.format("%.2f %d %b", this.scale, this.maxAge, this.clockwise);
	}

	public float getScale() {
		return scale;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public boolean isClockwise() {
		return clockwise;
	}
}
