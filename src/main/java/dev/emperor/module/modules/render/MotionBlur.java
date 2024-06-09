package dev.emperor.module.modules.render;

import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.NumberValue;

public class MotionBlur
extends Module {
    public final NumberValue blurAmount = new NumberValue("Amount", 7.0, 0.0, 10.0, 0.1);

    public MotionBlur() {
        super("MotionBlur", Category.Render);
    }
}

