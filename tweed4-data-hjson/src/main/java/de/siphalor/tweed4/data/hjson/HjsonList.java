package de.siphalor.tweed4.data.hjson;

import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataSerializer;
import org.hjson.JsonArray;
import org.hjson.JsonValue;

import java.util.AbstractList;

public class HjsonList extends AbstractList<JsonValue> implements DataList<JsonValue, HjsonList, HjsonObject> {
	private final JsonArray jsonArray;

	public HjsonList(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public HjsonList() {
		this(new JsonArray());
	}

	@Override
	public JsonValue getValue() {
		return jsonArray;
	}

	public JsonArray getJsonArray() {
		return jsonArray;
	}

	@Override
	public String getComment(int index) {
		return jsonArray.get(index).getBOLComment();
	}

	@Override
	public void setComment(int index, String comment) {
		jsonArray.setComment(index, comment);
	}

	@Override
	public DataSerializer<JsonValue, HjsonList, HjsonObject> getSerializer() {
		return HjsonSerializer.INSTANCE;
	}

	@Override
	public JsonValue get(int index) {
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
	public JsonValue set(int index, JsonValue element) {
		return jsonArray.set(index, element);
	}

	@Override
	public void add(int index, JsonValue element) {
		jsonArray.add(element);
		for (int i = jsonArray.size() - 1; i > index; i--) {
			jsonArray.set(i, jsonArray.get(i - 1));
		}
		jsonArray.set(index, element);
	}

	@Override
	public JsonValue remove(int index) {
		return jsonArray.remove(index);
	}
}
