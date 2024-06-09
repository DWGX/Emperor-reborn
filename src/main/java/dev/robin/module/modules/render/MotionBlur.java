package dev.robin.module.modules.render;

import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.NumberValue;

public class MotionBlur
extends Module {
    public final NumberValue blurAmount = new NumberValue("Amount", 7.0, 0.0, 10.0, 0.1);

    public MotionBlur() {
        super("MotionBlur", Category.Render);
    }
}

