package de.siphalor.tweed4.data.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.siphalor.tweed4.data.DataList;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class GsonList extends GsonValue implements DataList<GsonValue, GsonList, GsonObject> {
	GsonList(JsonElement jsonElement) {
		super(jsonElement);
	}

	@Override
	public int size() {
		return jsonElement.getAsJsonArray().size();
	}

	@Override
	public GsonValue get(Integer index) {
		return new GsonValue(jsonElement.getAsJsonArray().get(index));
	}

	@Override
	public GsonValue set(Integer index, byte value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, short value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, int value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, long value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, float value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, double value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, char value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, String value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, boolean value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonArray().set(index, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(Integer index, GsonValue value) {
		jsonElement.getAsJsonArray().set(index, value.getRaw());
		return value;
	}

	@Override
	public GsonList addList(Integer index) {
		JsonArray jsonArray = new JsonArray();
		jsonElement.getAsJsonArray().set(index, jsonArray);
		return new GsonList(jsonArray);
	}

	@Override
	public GsonObject addObject(Integer index) {
		JsonObject jsonObject = new JsonObject();
		jsonElement.getAsJsonArray().set(index, jsonObject);
		return new GsonObject(jsonObject);
	}

	@Override
	public void remove(Integer index) {
		jsonElement.getAsJsonArray().remove(index);
	}

	@Override
	@NotNull
	public Iterator<GsonValue> iterator() {
		return new Iterator<GsonValue>() {
			final Iterator<JsonElement> jsonElementIterator = jsonElement.getAsJsonArray().iterator();

			@Override
			public boolean hasNext() {
				return jsonElementIterator.hasNext();
			}

			@Override
			public GsonValue next() {
				return new GsonValue(jsonElementIterator.next());
			}
		};
	}
}
