package de.siphalor.tweed4.data.gson;

import com.google.gson.*;
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
		return setPrimitive(index, new JsonPrimitive(value));
	}

	@Override
	public GsonValue set(Integer index, short value) {
		return setPrimitive(index, new JsonPrimitive(value));
	}

	@Override
	public GsonValue set(Integer index, int value) {
		return setPrimitive(index, new JsonPrimitive(value));
	}

	@Override
	public GsonValue set(Integer index, long value) {
		return setPrimitive(index, new JsonPrimitive(value));
	}

	@Override
	public GsonValue set(Integer index, float value) {
		return setPrimitive(index, new JsonPrimitive(value));
	}

	@Override
	public GsonValue set(Integer index, double value) {
		return setPrimitive(index, new JsonPrimitive(value));
	}

	@Override
	public GsonValue set(Integer index, char value) {
		return setPrimitive(index, new JsonPrimitive(value));
	}

	@Override
	public GsonValue set(Integer index, String value) {
		return setPrimitive(index, new JsonPrimitive(value));
	}

	@Override
	public GsonValue set(Integer index, boolean value) {
		return setPrimitive(index, new JsonPrimitive(value));
	}

	private GsonValue setPrimitive(int index, JsonPrimitive primitive) {
		requireLength(index + 1);
		jsonElement.getAsJsonArray().set(index, primitive);
		return new GsonValue(primitive);
	}

	@Override
	public GsonValue set(Integer index, GsonValue value) {
		requireLength(index + 1);
		jsonElement.getAsJsonArray().set(index, value.getRaw());
		return value;
	}

	private void requireLength(int length) {
		JsonArray array = jsonElement.getAsJsonArray();
		if (length > array.size()) {
			for (int i = array.size(); i < length; i++) {
				array.add(JsonNull.INSTANCE);
			}
		}
	}

	@Override
	public GsonList addList(Integer index) {
		JsonArray jsonArray = new JsonArray();
		requireLength(index + 1);
		jsonElement.getAsJsonArray().set(index, jsonArray);
		return new GsonList(jsonArray);
	}

	@Override
	public GsonObject addObject(Integer index) {
		JsonObject jsonObject = new JsonObject();
		requireLength(index + 1);
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
