package dev.emperor.module.modules.render;

import dev.emperor.gui.clickgui.book.NewClickGui;
import dev.emperor.gui.clickgui.drop.DropdownClickGUI;
import dev.emperor.gui.clickgui.express.NormalClickGUI;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.ModeValue;
import org.lwjgl.input.Keyboard;

public class ClickGui
extends Module {
    public ModeValue<ClickGuiMode> mode = new ModeValue("Mode", (Enum[])ClickGuiMode.values(), (Enum)ClickGuiMode.Book);

    public ClickGui() {
        super("ClickGui", Category.Render);
        this.setKey(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        switch ((ClickGuiMode)((Object)this.mode.getValue())) {
            case Normal: {
                mc.displayGuiScreen(new NormalClickGUI());
                break;
            }
            case DropDown: {
                mc.displayGuiScreen(new DropdownClickGUI());
                break;
            }
            case Book: {
                mc.displayGuiScreen(NewClickGui.getInstance());
            }
        }
        this.setState(false);
    }

    public static enum ClickGuiMode {
        Normal,
        DropDown,
        Book;

    }
}

