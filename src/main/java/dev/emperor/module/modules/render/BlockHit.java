package dev.emperor.module.modules.render;

import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;

public class BlockHit
extends Module {
    public static final ModeValue<swords> Sword = new ModeValue("Style", (Enum[])swords.values(), (Enum)swords.Normal);
    public static final NumberValue Scale = new NumberValue("Scale", 0.4, 0.0, 4.0, 0.1);
    public static final NumberValue itemPosX = new NumberValue("ItemX", 0.0, -1.0, 1.0, 0.01);
    public static final NumberValue itemPosY = new NumberValue("ItemY", 0.0, -1.0, 1.0, 0.01);
    public static final NumberValue itemPosZ = new NumberValue("ItemZ", 0.0, -1.0, 1.0, 0.01);
    public static final NumberValue itemDistance = new NumberValue("ItemDistance", 1.0, 1.0, 5.0, 0.01);
    public static final NumberValue blockPosX = new NumberValue("BlockingX", 0.0, -1.0, 1.0, 0.01);
    public static final NumberValue blockPosY = new NumberValue("BlockingY", 0.0, -1.0, 1.0, 0.01);
    public static final NumberValue blockPosZ = new NumberValue("BlockingZ", 0.0, -1.0, 1.0, 0.01);
    public static final NumberValue SpeedSwing = new NumberValue("Swing-Speed", 4.0, 0.0, 20.0, 1.0);
    public static final NumberValue mcSwordPos = new NumberValue("MCPosOffset", 0.45, 0.0, 0.5, 0.01);
    public static final BoolValue fakeBlock = new BoolValue("Fake-Block", false);
    public static final BoolValue blockEverything = new BoolValue("Block-Everything", false);
    public static final BoolValue swing = new BoolValue("FluxSwing", false);
    public static final BoolValue oldBow = new BoolValue("1.7Bow", false);
    public static final BoolValue oldRod = new BoolValue("1.7Rod", false);
    public static final BoolValue oldSwing = new BoolValue("1.7Swing", false);

    public BlockHit() {
        super("BlockAnimations", Category.Render);
    }

    public static enum swords {
        Normal,
        SlideDown1,
        SlideDown2,
        Slide,
        Slide1,
        Minecraft,
        Remix,
        Exhibition,
        Avatar,
        Swang,
        Tap1,
        Tap2,
        Poke,
        Push1,
        Push2,
        Up,
        Akrien,
        VisionFX,
        Swong,
        Swank,
        SigmaOld,
        ETB,
        Rotate360,
        SmoothFloat,
        Strange,
        Reverse,
        Zoom,
        Move,
        Stab,
        Jello,
        Old,
        Flux,
        Stella,
        Tifality,
        OldExhibition,
        Smooth;

    }
}

