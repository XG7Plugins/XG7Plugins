package com.xg7plugins.data.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.xg7plugins.utils.item.Item;

import java.io.IOException;

public class DefaultItemTypeAdapter extends TypeAdapter<Item> {
    @Override
    public void write(JsonWriter out, Item value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(value.toString());
    }

    @Override
    public Item read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String base64 = in.nextString();
        return Item.from(Item.fromJson(base64));
    }
}
