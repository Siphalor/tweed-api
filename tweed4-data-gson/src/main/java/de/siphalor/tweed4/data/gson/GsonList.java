package de.siphalor.tweed4.data.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataSerializer;

import java.util.AbstractList;

public class GsonList extends AbstractList<JsonElement> implements DataList<JsonElement, GsonList, GsonObject> {
	private final JsonArray jsonArray;

	public GsonList(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public GsonList() {
		this(new JsonArray());
	}

	@Override
	public JsonElement getValue() {
		return jsonArray;
	}

	@Override
	public int size() {
		return jsonArray.size();
	}

	@Override
	public JsonElement get(int index) {
		return jsonArray.get(index);
	}

	@Override
	public JsonElement set(int index, JsonElement element) {
		return super.set(index, element);
	}

	@Override
	public void add(int index, JsonElement element) {
		super.add(index, element);
	}

	@Override
	public JsonElement remove(int index) {
		return super.remove(index);
	}

	@Override
	public String getComment(int index) {
		return null;
	}

	@Override
	public void setComment(int index, String comment) {

	}

	@Override
	public DataSerializer<JsonElement, GsonList, GsonObject> getSerializer() {
		return GsonSerializer.INSTANCE;
	}

}
