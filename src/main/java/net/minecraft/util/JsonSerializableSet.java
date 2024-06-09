package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.util.IJsonSerializable;

public class JsonSerializableSet
extends ForwardingSet<String>
implements IJsonSerializable {
    private final Set<String> underlyingSet = Sets.newHashSet();

    @Override
    public void fromJson(JsonElement json) {
        if (json.isJsonArray()) {
            for (JsonElement jsonelement : json.getAsJsonArray()) {
                this.add(jsonelement.getAsString());
            }
        }
    }

    @Override
    public JsonElement getSerializableElement() {
        JsonArray jsonarray = new JsonArray();
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            String s2 = (String)iterator.next();
            jsonarray.add((JsonElement)new JsonPrimitive(s2));
        }
        return jsonarray;
    }

    protected Set<String> delegate() {
        return this.underlyingSet;
    }
}

