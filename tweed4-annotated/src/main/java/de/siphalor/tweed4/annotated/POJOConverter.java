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

package de.siphalor.tweed4.annotated;

import com.google.common.base.CaseFormat;
import com.google.common.collect.HashMultimap;
import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.config.ConfigCategory;
import de.siphalor.tweed4.config.ConfigFile;
import de.siphalor.tweed4.config.TweedRegistry;
import de.siphalor.tweed4.config.constraints.AnnotationConstraint;
import de.siphalor.tweed4.config.entry.AbstractValueConfigEntry;
import de.siphalor.tweed4.config.entry.ConfigEntry;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;
import de.siphalor.tweed4.config.fixers.ConfigEntryFixer;
import de.siphalor.tweed4.config.value.ReferenceConfigValue;
import de.siphalor.tweed4.config.value.serializer.ConfigSerializers;
import de.siphalor.tweed4.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed4.config.value.serializer.NullableSerializer;
import de.siphalor.tweed4.config.value.serializer.ReflectiveNullable;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import de.siphalor.tweed4.data.serializer.ConfigDataSerializer;
import de.siphalor.tweed4.tailor.Tailor;
import de.siphalor.tweed4.util.ReflectionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;

public class POJOConverter {
	private static final HashMultimap<Class<?>, POJOConfigEntryMapper> ENTRY_MAPPERS = HashMultimap.create();
	private static final HashMultimap<Class<?>, POJOConfigValueSerializerFactory<Object, ConfigValueSerializer<Object>>> SERIALIZER_FACTORIES = HashMultimap.create();

	public static void registerEntryMapper(Class<?> clazz, POJOConfigEntryMapper entryMapper) {
		ENTRY_MAPPERS.put(clazz, entryMapper);
	}

	/**
	 * Registers a custom serializer for a type.
	 * @param clazz The class that should be captured
	 * @param serializer The serializer to use
	 */
	public static void registerSerializer(Class<?> clazz, ConfigValueSerializer<Object> serializer) {
		SERIALIZER_FACTORIES.put(clazz, (value, clazz1, type) -> serializer);
	}

	public static <T, Serializer extends ConfigValueSerializer<T>> void registerSerializerFactory(Class<? super T> clazz, POJOConfigValueSerializerFactory<T, Serializer> serializerFactory) {
		//noinspection unchecked
		SERIALIZER_FACTORIES.put(clazz, (POJOConfigValueSerializerFactory<Object, ConfigValueSerializer<Object>>) serializerFactory);
	}

	public static ConfigFile toConfigFile(Object pojo, String fallbackFileName) throws RuntimeException {
		ATweedConfig tweedConfig = pojo.getClass().getAnnotation(ATweedConfig.class);
		if (tweedConfig == null) {
			throw new RuntimeException("Tweed POJOs need the ATweedConfig annotation!");
		}
		ConfigCategory rootCategory = toCategory(pojo, tweedConfig.casing());
		rootCategory.setScope(tweedConfig.scope());
		rootCategory.setEnvironment(tweedConfig.environment());
		String file = tweedConfig.file();
		if (file.isEmpty()) {
			file = fallbackFileName;
		}
		//noinspection deprecation
		ConfigDataSerializer<?, ?, ?> serializer = TweedRegistry.SERIALIZERS.getOrEmpty(new Identifier(tweedConfig.serializer())).orElse(TweedRegistry.getDefaultSerializer());

		ConfigFile configFile = new ConfigFile(file, serializer, rootCategory);

		for (Method method : pojo.getClass().getDeclaredMethods()) {
			AConfigFixer configFixer = method.getAnnotation(AConfigFixer.class);
			if (configFixer != null && method.getParameterCount() == 2) {
				Class<?>[] args = method.getParameterTypes();
				if (args[0] == DataObject.class && args[1] == DataObject.class) {
					method.setAccessible(true);

					ConfigEntryFixer configEntryFixer = new ConfigEntryFixer() {
						@Override
						public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
						void fix(O dataObject, String propertyName, O mainCompound) {
							try {
								method.invoke(pojo, dataObject, mainCompound);
							} catch (IllegalAccessException | InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					};

					configFile.register(configFixer.value().isEmpty() ? "" : configFixer.value() + Tweed.PATH_DELIMITER + "_", configEntryFixer);
				}
			}
		}

		configFile.addTailorAnnotations(pojo.getClass().getAnnotations());

		Tailor tailor;
		for (String tailorId : tweedConfig.tailors()) {
			tailor = TweedRegistry.TAILORS.get(new Identifier(tailorId));
			if (tailor != null)
				tailor.process(configFile);
		}

		return configFile;
	}

	public static ConfigCategory toCategory(Object pojo, CaseFormat casing) {
		ConfigCategory configCategory = new ConfigCategory();

		if (pojo.getClass().isAnnotationPresent(AConfigBackground.class)) {
			String tex = pojo.getClass().getAnnotation(AConfigBackground.class).value();
			if (!"".equals(tex)) {
				configCategory.setBackgroundTexture(new Identifier(tex));
			}
		}

		for (Field field : ReflectionUtil.getAllDeclaredFields(pojo.getClass())) {
			try {
				addToCategory(configCategory, pojo, field, casing);
			} catch (StackOverflowError e) {
				Tweed.LOGGER.error("Failed to unwrap unknown object of type \"" + field.getType() + "\" in field \"" + field.getName() + "\". This usually indicates recursively nested types without serializers.");
			}
		}

		for (Method method : ReflectionUtil.getAllDeclaredMethods(pojo.getClass())) {
			// Check for listening methods
			AConfigListener configListener = method.getAnnotation(AConfigListener.class);
			if (configListener != null) {
				if (method.getParameterCount() > 0) {
					Tweed.LOGGER.error("Method " + method.getName() + " on " + pojo.getClass().getCanonicalName() + " must have no parameters to be a listener!");
				} else {
					if (configListener.value().isEmpty()) {
						method.setAccessible(true);
						configCategory.setReloadListener(() -> {
							try {
								method.invoke(pojo);
							} catch (IllegalAccessException | InvocationTargetException e) {
								e.printStackTrace();
							}
						});
					} else {
						ConfigEntry<?> configEntry = configCategory.get(configListener.value());
						if (configEntry != null) {
							if (configEntry instanceof ConfigCategory) {
								((ConfigCategory) configEntry).setReloadListener(() -> {
									try {
										method.invoke(pojo);
									} catch (IllegalAccessException | InvocationTargetException e) {
										e.printStackTrace();
									}
								});
							} else if (configEntry instanceof ValueConfigEntry) {
								((ValueConfigEntry<?>) configEntry).setReloadListener(o -> {
									try {
										method.invoke(pojo);
									} catch (IllegalAccessException | InvocationTargetException e) {
										e.printStackTrace();
									}
								});
							} else {
								Tweed.LOGGER.error("Couldn't bind config reload listener on " + pojo.getClass().getCanonicalName() + " for " + configListener.value() + " to an entry.");
							}
						} else {
							Tweed.LOGGER.error("Couldn't find a config entry to bind the reload listener to in " + pojo.getClass().getCanonicalName());
						}
					}
				}
			}
		}

		return configCategory;
	}

	public static void addToCategory(ConfigCategory configCategory, Object pojo, Field field, CaseFormat casing) {
		if (pojo.getClass() == field.getGenericType()) {
			Tweed.LOGGER.error("Found recursively nested type in config entry, skipping this field: " + field.getName() + " in " + pojo.getClass());
			return;
		}

		field.setAccessible(true);

		if (field.isAnnotationPresent(AConfigExclude.class)) return;
		if (Modifier.isPrivate(field.getModifiers())) return;

		if (field.isAnnotationPresent(AConfigTransitive.class)) {
			try {
				Object o = field.get(pojo);
				if (o == null) {
					try {
						Constructor<?> constructor = field.getType().getConstructor();
						o = constructor.newInstance();
						field.set(pojo, o);
					} catch (NoSuchMethodException | InstantiationException | InvocationTargetException e) {
						e.printStackTrace();
						return;
					}
				}
				Object finalO = o;

				Arrays.stream(ReflectionUtil.getAllDeclaredFields(o.getClass())).forEach(field1 -> addToCategory(configCategory, finalO, field1, casing));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			Pair<String, ConfigEntry<?>> converted = toEntry(pojo, field, casing);
			if (converted == null) return;

			configCategory.register(converted.getLeft(), converted.getRight());
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Pair<String, ConfigEntry<?>> toEntry(Object pojo, Field field, CaseFormat casing) {
		try {
			Object fieldValue = field.get(pojo);
			ConfigSerializers.SerializerResolver resolver = new ConfigSerializers.SerializerResolver() {
				@Override
				public <T> ConfigValueSerializer<T> resolve(T value, Class<T> clazz, Type type) {
					Class<?> curClass = clazz;
					while (true) {
						for (POJOConfigValueSerializerFactory<Object, ConfigValueSerializer<Object>> serializerFactory : SERIALIZER_FACTORIES.get(curClass)) {
							ConfigValueSerializer<Object> serializer = serializerFactory.create(value, (Class<Object>) clazz, type);
							if (serializer != null) {
								return (ConfigValueSerializer<T>) serializer;
							}
						}

						Class<?> superclass = curClass.getSuperclass();
						if (superclass == null) {
							break;
						}
						curClass = superclass;
					}
					return ConfigSerializers.deduce(value, clazz, type, this);
				}
			};

			ConfigEntry<?> entry = null;
			annotations:
			for (Annotation annotation : field.getAnnotations()) {
				for (POJOConfigEntryMapper entryMapper : ENTRY_MAPPERS.get(annotation.getClass())) {
					entry = entryMapper.map(field, fieldValue, resolver);
					if (entry != null) {
						break annotations;
					}
				}
			}
			if (entry == null) {
				for (POJOConfigEntryMapper entryMapper : ENTRY_MAPPERS.get(field.getType())) {
					entry = entryMapper.map(field, fieldValue, resolver);
					if (entry != null) {
						break;
					}
				}
			}

			if (entry == null) {
				ConfigValueSerializer<Object> valueSerializer = ConfigSerializers.deduce(
						fieldValue, (Class<Object>) field.getType(), field.getGenericType(), resolver, false
				);

				if (valueSerializer != null) {
					if (field.isAnnotationPresent(ReflectiveNullable.class)) {
						valueSerializer = new NullableSerializer<>(valueSerializer);
					}
					entry = new ValueConfigEntry(new ReferenceConfigValue(pojo, field), valueSerializer);
				} else {
					if (fieldValue == null) {
						try {
							Constructor<?> constructor = field.getType().getConstructor();
							fieldValue = constructor.newInstance();
							field.set(pojo, fieldValue);
						} catch (NoSuchMethodException | InstantiationException | InvocationTargetException e) {
							e.printStackTrace();
							return null;
						}
					}
					entry = toCategory(fieldValue, casing);
				}
			}

			String name = processConfigEntry(entry, field, casing);
			return new Pair<>(name, entry);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String processConfigEntry(ConfigEntry<?> entry, Field field, CaseFormat casing) {
		String name = CaseFormat.LOWER_CAMEL.to(casing, field.getName());
		if (field.isAnnotationPresent(AConfigEntry.class)) {
			AConfigEntry configData = field.getAnnotation(AConfigEntry.class);
			if (!"".equals(configData.name())) {
				name = configData.name();
			}
			entry.setComment(configData.comment());
			entry.setScope(configData.scope());
			entry.setEnvironment(configData.environment());

			// Add constraints
			if (entry instanceof AbstractValueConfigEntry) {
				Class<?> clazz;
				for (AConfigConstraint aConstraint : configData.constraints()) {
					clazz = aConstraint.value();
					if (AnnotationConstraint.class.isAssignableFrom(clazz)) {
						try {
							//noinspection rawtypes,unchecked
							Constructor<AnnotationConstraint> constructor = (Constructor<AnnotationConstraint>) clazz.getConstructor();
							constructor.setAccessible(true);
							AnnotationConstraint<?> constraint = constructor.newInstance();
							//noinspection rawtypes
							constraint.fromAnnotationParam(aConstraint.param(), ((ValueConfigEntry) entry).getType());

							//noinspection unchecked,rawtypes
							((AbstractValueConfigEntry) entry).addConstraint(constraint);
						} catch (ReflectiveOperationException ignored) {
						}
					}
				}
			}
		}

		return name;
	}
}
