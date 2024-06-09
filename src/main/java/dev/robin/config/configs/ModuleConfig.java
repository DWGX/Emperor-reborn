package dev.robin.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.robin.Client;
import dev.robin.config.Config;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ColorValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.module.values.Value;
import java.awt.Color;

public class ModuleConfig
extends Config {
    public ModuleConfig() {
        super("modules.json");
    }

    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();
        for (Module module : Client.instance.moduleManager.getModules()) {
            JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("state", Boolean.valueOf(module.getState()));
            moduleObject.addProperty("key", (Number)module.getKey());
            JsonObject valuesObject = new JsonObject();
            for (Value<?> value : module.getValues()) {
                if (value instanceof NumberValue) {
                    valuesObject.addProperty(value.getName(), (Number)((NumberValue)value).getValue());
                    continue;
                }
                if (value instanceof BoolValue) {
                    valuesObject.addProperty(value.getName(), (Boolean)((BoolValue)value).getValue());
                    continue;
                }
                if (value instanceof ModeValue) {
                    valuesObject.addProperty(value.getName(), ((ModeValue)value).getConfigValue());
                    continue;
                }
                if (!(value instanceof ColorValue)) continue;
                valuesObject.addProperty(value.getName(), (Number)((ColorValue)value).getColor());
            }
            moduleObject.add("values", (JsonElement)valuesObject);
            object.add(module.getName(), (JsonElement)moduleObject);
        }
        return object;
    }

    @Override
    public void loadConfig(JsonObject object) {
        for (Module module : Client.instance.moduleManager.getModules()) {
            if (!object.has(module.getName())) continue;
            JsonObject moduleObject = object.get(module.getName()).getAsJsonObject();
            if (moduleObject.has("state")) {
                module.setState(moduleObject.get("state").getAsBoolean());
            }
            if (moduleObject.has("key")) {
                module.setKey(moduleObject.get("key").getAsInt());
            }
            if (!moduleObject.has("values")) continue;
            JsonObject valuesObject = moduleObject.get("values").getAsJsonObject();
            for (Value<?> value : module.getValues()) {
                if (!valuesObject.has(value.getName())) continue;
                JsonElement theValue = valuesObject.get(value.getName());
                if (value instanceof NumberValue) {
                    ((NumberValue)value).setValue(theValue.getAsNumber().doubleValue());
                    continue;
                }
                if (value instanceof BoolValue) {
                    ((BoolValue)value).setValue(theValue.getAsBoolean());
                    continue;
                }
                if (value instanceof ModeValue) {
                    ((ModeValue)value).setMode(theValue.getAsString());
                    continue;
                }
                if (!(value instanceof ColorValue)) continue;
                Color color = new Color(theValue.getAsInt());
                ((ColorValue)value).setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()).getRGB());
            }
        }
    }
}

