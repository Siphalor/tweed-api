package de.siphalor.tweed.config;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.config.annotated.*;
import de.siphalor.tweed.config.constraints.AnnotationConstraint;
import de.siphalor.tweed.config.entry.AbstractBasicEntry;
import de.siphalor.tweed.config.entry.ConfigEntry;
import de.siphalor.tweed.config.entry.ValueConfigEntry;
import de.siphalor.tweed.config.fixers.ConfigEntryFixer;
import de.siphalor.tweed.config.value.ConfigValue;
import de.siphalor.tweed.config.value.ReferenceConfigValue;
import de.siphalor.tweed.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed.data.DataObject;
import de.siphalor.tweed.data.serializer.ConfigDataSerializer;
import de.siphalor.tweed.data.serializer.HjsonSerializer;
import de.siphalor.tweed.tailor.Tailor;
import de.siphalor.tweed.util.ReflectionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class POJOConverter {
	private static final Map<Class<?>, ConfigValueSerializer<?>> SERIALIZER_MAP = new HashMap<>();

	/**
	 * Registers a custom serializer for a type.
	 * @param clazz The class that should be captured
	 * @param serializer The serializer to use
	 */
	public static void registerSerializer(Class<?> clazz, ConfigValueSerializer<?> serializer) {
		SERIALIZER_MAP.put(clazz, serializer);
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
		ConfigDataSerializer<?> serializer = TweedRegistry.SERIALIZERS.getOrEmpty(new Identifier(tweedConfig.serializer())).orElse(HjsonSerializer.INSTANCE);

		ConfigFile configFile = new ConfigFile(file, serializer, rootCategory);

		for (Method method : pojo.getClass().getDeclaredMethods()) {
			AConfigFixer configFixer = method.getAnnotation(AConfigFixer.class);
			if (configFixer != null && method.getParameterCount() == 2) {
				Class<?>[] args = method.getParameterTypes();
				if (args[0] == DataObject.class && args[1] == DataObject.class) {
					method.setAccessible(true);

					ConfigEntryFixer configEntryFixer = new ConfigEntryFixer() {
						@Override
						public void fix(DataObject<?> dataObject, String propertyName, DataObject<?> mainCompound) {
							try {
								method.invoke(pojo, dataObject, mainCompound);
							} catch (IllegalAccessException | InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					};

					configFile.register(configFixer.value().isEmpty() ? "" : configFixer.value() + ".", configEntryFixer);
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
			Object entryObject = field.get(pojo);
			ConfigValueSerializer<?> valueSerializer = ConfigValue.serializer(entryObject, field.getType());
			if (valueSerializer == null) {
				 valueSerializer = SERIALIZER_MAP.get(field.getType());
			}
			AbstractBasicEntry basicEntry;
			if (valueSerializer == null) {
				if (entryObject == null) {
					try {
						Constructor<?> constructor = field.getType().getConstructor();
						entryObject = constructor.newInstance();
						field.set(pojo, entryObject);
					} catch (NoSuchMethodException | InstantiationException | InvocationTargetException e) {
						e.printStackTrace();
						return null;
					}
				}
				basicEntry = toCategory(entryObject, casing);
			} else {
				basicEntry = new ValueConfigEntry(new ReferenceConfigValue(pojo, field), valueSerializer);
			}

			String name = CaseFormat.LOWER_CAMEL.to(casing, field.getName());
			if (field.isAnnotationPresent(AConfigEntry.class)) {
				AConfigEntry configData = field.getAnnotation(AConfigEntry.class);
				if (!"".equals(configData.name())) {
					name = configData.name();
				}
				basicEntry.setComment(configData.comment());
				basicEntry.setScope(configData.scope());
				basicEntry.setEnvironment(configData.environment());

				// Add constraints
				if (basicEntry instanceof ValueConfigEntry) {
					Class<?> clazz;
					for (AConfigConstraint aConstraint : configData.constraints()) {
						clazz = aConstraint.value();
						if (AnnotationConstraint.class.isAssignableFrom(clazz)) {
							try {
								Constructor<AnnotationConstraint> constructor = (Constructor<AnnotationConstraint>) clazz.getConstructor();
								constructor.setAccessible(true);
								AnnotationConstraint<?> constraint = constructor.newInstance();
								constraint.fromAnnotationParam(aConstraint.param(), ((ValueConfigEntry) basicEntry).getType());

								((ValueConfigEntry) basicEntry).addConstraint(constraint);
							} catch (NoSuchMethodException | InstantiationException | InvocationTargetException ignored) {
							}
						}
					}
				}
			}
			return new Pair<>(name, basicEntry);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
