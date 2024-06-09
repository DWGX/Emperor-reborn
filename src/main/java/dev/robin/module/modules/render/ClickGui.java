package dev.robin.module.modules.render;

import dev.robin.gui.clickgui.book.NewClickGui;
import dev.robin.gui.clickgui.drop.DropdownClickGUI;
import dev.robin.gui.clickgui.express.NormalClickGUI;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.ModeValue;
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

