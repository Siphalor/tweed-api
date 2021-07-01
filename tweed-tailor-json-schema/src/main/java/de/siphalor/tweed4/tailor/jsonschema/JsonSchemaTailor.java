package de.siphalor.tweed4.tailor.jsonschema;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.TweedInitializer;
import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigFile;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.TweedRegistry;
import de.siphalor.tweed4.config.entry.ConstantConfigEntry;
import de.siphalor.tweed4.config.value.ConfigValue;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import de.siphalor.tweed4.tailor.Tailor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class JsonSchemaTailor extends Tailor implements TweedInitializer {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	@Override
	public void tweedRegister() {
		Registry.register(TweedRegistry.TAILORS, new Identifier(Tweed.MOD_ID, "json_schema"), this);
	}

	@Override
	public void process(ConfigFile configFile) {
		JsonObject rootObject = new JsonObject();
		rootObject.addProperty("$schema", "http://json-schema.org/draft-07/schema");
		rootObject.addProperty("type", "object");
		rootObject.addProperty("additionalProperties", false);

		ConvertingObject rootConverter = new ConvertingObject(rootObject);
		rootConverter.set("$schema", "");
		configFile.getRootCategory().write(rootConverter, "", ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);

		String schemaFileName = configFile.getName() + ".schema.json";
		File file = FabricLoader.getInstance().getConfigDir().resolve(schemaFileName).toFile();
		try (FileWriter fileWriter = new FileWriter(file)) {
			GSON.toJson(rootObject, fileWriter);
			configFile.getRootCategory().register("$schema", new ConstantConfigEntry<>("./" + schemaFileName, ConfigValue.stringSerializer()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ConvertingValue implements DataValue<JsonObject> {
		private final JsonObject jsonObject;

		public ConvertingValue(JsonObject jsonObject) {
			this.jsonObject = jsonObject;
		}

		public ConvertingValue(String type) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("type", type);
		}

		@Override
		public void setComment(String comment) {
			jsonObject.addProperty("description", comment);
			jsonObject.addProperty("descriptionMd", comment);
		}

		@Override
		public String getComment() {
			return jsonObject.get("description").getAsString();
		}

		@Override
		public boolean isNumber() {
			return "number".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isByte() {
			return "number".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isShort() {
			return "number".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isInt() {
			return "number".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isLong() {
			return "number".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isFloat() {
			return "number".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isDouble() {
			return "number".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isChar() {
			return "string".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isString() {
			return "string".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isBoolean() {
			return "boolean".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isObject() {
			return "object".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public boolean isList() {
			return "array".equals(jsonObject.get("type").getAsString());
		}

		@Override
		public byte asByte() {
			throw new UnsupportedOperationException();
		}

		@Override
		public short asShort() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int asInt() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long asLong() {
			throw new UnsupportedOperationException();
		}

		@Override
		public float asFloat() {
			throw new UnsupportedOperationException();
		}

		@Override
		public double asDouble() {
			throw new UnsupportedOperationException();
		}

		@Override
		public char asChar() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String asString() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean asBoolean() {
			throw new UnsupportedOperationException();
		}

		@Override
		public DataObject<JsonObject> asObject() {
			return new ConvertingObject(jsonObject);
		}

		@Override
		public DataList<JsonObject> asList() {
			return new ConvertingList(jsonObject);
		}

		@Override
		public JsonObject getRaw() {
			return jsonObject;
		}
	}

	private static class ConvertingObject extends ConvertingValue implements DataObject<JsonObject> {
		private final JsonObject propertiesObject;

		public ConvertingObject(JsonObject jsonObject) {
			super(jsonObject);
			JsonObject temp = jsonObject.getAsJsonObject("properties");
			if (temp == null) {
				temp = new JsonObject();
				jsonObject.add("properties", temp);
			}
			propertiesObject = temp;
		}

		public ConvertingObject() {
			super("object");
			propertiesObject = new JsonObject();
		}

		@Override
		public int size() {
			return propertiesObject.size();
		}

		@Override
		public boolean has(String key) {
			return propertiesObject.has(key);
		}

		@Override
		public DataValue<JsonObject> set(String key, DataValue<JsonObject> value) {
			throw new UnsupportedOperationException();
		}

		private DataValue<JsonObject> createProperty(String key, String type) {
			ConvertingValue value = new ConvertingValue(type);
			propertiesObject.add(key, value.getRaw());
			return value;
		}

		@Override
		public DataValue<JsonObject> set(String key, boolean value) {
			return createProperty(key, "boolean");
		}

		@Override
		public DataValue<JsonObject> set(String key, String value) {
			return createProperty(key, "string");
		}

		@Override
		public DataValue<JsonObject> set(String key, char value) {
			return createProperty(key, "string");
		}

		@Override
		public DataValue<JsonObject> set(String key, double value) {
			return createProperty(key, "number");
		}

		@Override
		public DataValue<JsonObject> set(String key, float value) {
			return createProperty(key, "number");
		}

		@Override
		public DataValue<JsonObject> set(String key, long value) {
			return createProperty(key, "number");
		}

		@Override
		public DataValue<JsonObject> set(String key, int value) {
			return createProperty(key, "number");
		}

		@Override
		public DataValue<JsonObject> set(String key, short value) {
			return createProperty(key, "number");
		}

		@Override
		public DataValue<JsonObject> set(String key, byte value) {
			return createProperty(key, "number");
		}

		@Override
		public DataObject<JsonObject> addObject(String key) {
			ConvertingObject object = new ConvertingObject();
			propertiesObject.add(key, object.getRaw());
			return object;
		}

		@Override
		public DataList<JsonObject> addList(String key) {
			ConvertingList list = new ConvertingList();
			propertiesObject.add(key, list.getRaw());
			return list;
		}

		@Override
		public DataValue<JsonObject> get(String key) {
			return new ConvertingValue(propertiesObject.getAsJsonObject(key));
		}

		@Override
		public void remove(String key) {
			propertiesObject.remove(key);
		}

		@NotNull
		@Override
		public Iterator<Pair<String, DataValue<JsonObject>>> iterator() {
			return propertiesObject.entrySet().stream().map(entry -> Pair.of(
					entry.getKey(),
					(DataValue<JsonObject>) new ConvertingValue(entry.getValue().getAsJsonObject())
			)).iterator();
		}
	}

	private static class ConvertingList extends ConvertingValue implements DataList<JsonObject> {
		public ConvertingList(JsonObject jsonObject) {
			super(jsonObject);
		}

		public ConvertingList() {
			super("array");
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public DataValue<JsonObject> get(Integer index) {
			throw new UnsupportedOperationException();
		}

		private DataValue<JsonObject> addProperty(JsonObject property) {
			JsonObject jsonObject = getRaw();

			if (jsonObject.has("items")) {
				JsonObject items = jsonObject.getAsJsonObject("items");
				if (items.has("oneOf")) {
					JsonArray oneOf = items.getAsJsonArray("oneOf");
					for (JsonElement element : oneOf) {
						if (property.get("type").getAsString().equals(element.getAsJsonObject().get("type").getAsString())) {
							return new ConvertingValue(element.getAsJsonObject());
						}
					}
					oneOf.add(property);
				} else {
					JsonObject newItems = new JsonObject();
					JsonArray oneOf = new JsonArray();
					newItems.add("oneOf", oneOf);
					oneOf.add(items);
					oneOf.add(property);
					jsonObject.add("items", newItems);
				}
			} else {
				jsonObject.add("items", property);
			}
			return new ConvertingValue(property);
		}

		private DataValue<JsonObject> addType(String type) {
			JsonObject property = new JsonObject();
			property.addProperty("type", type);
			return addProperty(property);
		}

		@Override
		public DataValue<JsonObject> set(Integer index, byte value) {
			return addType("number");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, short value) {
			return addType("number");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, int value) {
			return addType("number");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, long value) {
			return addType("number");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, float value) {
			return addType("number");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, double value) {
			return addType("number");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, char value) {
			return addType("string");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, String value) {
			return addType("string");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, boolean value) {
			return addType("boolean");
		}

		@Override
		public DataValue<JsonObject> set(Integer index, DataValue<JsonObject> value) {
			return addProperty(value.getRaw());
		}

		@Override
		public DataList<JsonObject> addList(Integer index) {
			return addType("array").asList();
		}

		@Override
		public DataObject<JsonObject> addObject(Integer index) {
			return addType("object").asObject();
		}

		@Override
		public void remove(Integer index) {

		}

		@NotNull
		@Override
		public Iterator<DataValue<JsonObject>> iterator() {
			throw new UnsupportedOperationException();
		}
	}
}
