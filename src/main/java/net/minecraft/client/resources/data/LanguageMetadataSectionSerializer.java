package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.util.JsonUtils;

public class LanguageMetadataSectionSerializer
extends BaseMetadataSectionSerializer<LanguageMetadataSection> {
    public LanguageMetadataSection deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        HashSet set = Sets.newHashSet();
        for (Map.Entry entry : jsonobject.entrySet()) {
            String s2 = (String)entry.getKey();
            JsonObject jsonobject1 = JsonUtils.getJsonObject((JsonElement)entry.getValue(), "language");
            String s1 = JsonUtils.getString(jsonobject1, "region");
            String s22 = JsonUtils.getString(jsonobject1, "name");
            boolean flag = JsonUtils.getBoolean(jsonobject1, "bidirectional", false);
            if (s1.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + s2 + "'->region: empty value");
            }
            if (s22.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + s2 + "'->name: empty value");
            }
            if (set.add(new Language(s2, s1, s22, flag))) continue;
            throw new JsonParseException("Duplicate language->'" + s2 + "' defined");
        }
        return new LanguageMetadataSection(set);
    }

    @Override
    public String getSectionName() {
        return "language";
    }
}

