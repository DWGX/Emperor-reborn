package dev.robin.command.commands;

import dev.robin.Client;
import dev.robin.command.Command;
import dev.robin.module.Module;
import dev.robin.utils.DebugUtil;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class BindsCommand
extends Command {
    public BindsCommand() {
        super("binds");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void run(String[] args) {
        for (Module module : Client.instance.moduleManager.getModules()) {
            if (module.getKey() == -1) continue;
            DebugUtil.log("\u00a7a[Binds]\u00a7f" + module.name + " :" + Keyboard.getKeyName((int)module.key));
        }
    }
}

