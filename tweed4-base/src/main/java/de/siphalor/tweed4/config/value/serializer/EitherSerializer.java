/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4.config.value.serializer;

import com.mojang.datafixers.util.Either;
import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.Optional;

/**
 * A serializer for {@link Either}s
 * @since 1.3.2
 */
public class EitherSerializer<A, B> extends ConfigValueSerializer<Either<A, B>> {
	private final ConfigValueSerializer<A> leftSerializer;
	private final ConfigValueSerializer<B> rightSerializer;

	public EitherSerializer(ConfigValueSerializer<A> leftSerializer, ConfigValueSerializer<B> rightSerializer) {
		this.leftSerializer = leftSerializer;
		this.rightSerializer = rightSerializer;
	}

	@Override
	public <V> Either<A, B> read(DataSerializer<V> serializer, V value) throws ConfigReadException {
		try {
			return Either.left(leftSerializer.read(serializer, value));
		} catch (Exception e) {
			try {
				return Either.right(rightSerializer.read(serializer, value));
			} catch (Exception e2) {
				throw new ConfigReadException("Failed to deserialize either of two values:\n    " + e + "\nand " + e2);
			}
		}
	}

	@Override
	public <V> Object write(DataSerializer<V> serializer, Either<A, B> value) {
		return value.map(a -> leftSerializer.write(serializer, a), b -> rightSerializer.write(serializer, b));
	}

	@Override
	public Either<A, B> read(PacketByteBuf packetByteBuf) {
		if (packetByteBuf.readBoolean()) {
			return Either.right(rightSerializer.read(packetByteBuf));
		} else {
			return Either.left(leftSerializer.read(packetByteBuf));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Either<A, B> value) {
		Optional<A> leftOpt = value.left();
		if (leftOpt.isPresent()) {
			packetByteBuf.writeBoolean(false);
			leftSerializer.write(packetByteBuf, leftOpt.get());
		} else {
			packetByteBuf.writeBoolean(true);
			//noinspection OptionalGetWithoutIsPresent
			rightSerializer.write(packetByteBuf, value.right().get());
		}
	}

	@Override
	public Either<A, B> copy(Either<A, B> value) {
		Optional<A> leftOpt = value.left();
		//noinspection OptionalIsPresent
		if (leftOpt.isPresent()) {
			return Either.left(leftSerializer.copy(leftOpt.get()));
		} else {
			//noinspection OptionalGetWithoutIsPresent
			return Either.right(rightSerializer.copy(value.right().get()));
		}
	}

	@Override
	public String asString(Either<A, B> value) {
		return value.map(leftSerializer::asString, rightSerializer::asString);
	}

	@Override
	public Class<Either<A, B>> getType() {
		//noinspection unchecked
		return ((Class<Either<A,B>>)(Object) Either.class);
	}
}
