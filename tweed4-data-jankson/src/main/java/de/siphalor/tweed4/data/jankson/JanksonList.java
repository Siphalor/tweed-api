package de.siphalor.tweed4.data.jankson;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataSerializer;

import java.util.AbstractList;

public class JanksonList extends AbstractList<JsonElement> implements DataList<JsonElement, JanksonList, JanksonObject> {
	private final JsonArray jsonArray;

	public JanksonList(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public JanksonList() {
		this(new JsonArray());
	}

	@Override
	public JsonElement getValue() {
		return jsonArray;
	}

	public JsonArray getJsonArray() {
		return jsonArray;
	}

	@Override
	public String getComment(int index) {
		return jsonArray.getComment(index);
	}

	@Override
	public void setComment(int index, String comment) {
		jsonArray.setComment(index, comment);
	}

	@Override
	public DataSerializer<JsonElement, JanksonList, JanksonObject> getSerializer() {
		return JanksonSerializer.INSTANCE;
	}

	@Override
	public JsonElement get(int index) {
		return jsonArray.get(index);
	}

	@Override
	public int size() {
		return jsonArray.size();
	}

	@Override
	public boolean isEmpty() {
		return jsonArray.isEmpty();
	}

	@Override
	public JsonElement set(int index, JsonElement element) {
		return jsonArray.set(index, element);
	}

	@Override
	public void add(int index, JsonElement element) {
		jsonArray.add(index, element);
	}

	@Override
	public JsonElement remove(int index) {
		return jsonArray.remove(index);
	}
}
