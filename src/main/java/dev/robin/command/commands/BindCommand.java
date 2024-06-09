package dev.robin.command.commands;

import dev.robin.Client;
import dev.robin.command.Command;
import dev.robin.module.Module;
import dev.robin.utils.client.HelperUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class BindCommand
extends Command {
    public BindCommand() {
        super("bind", "b");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        String prefix = "";
        boolean flag = false;
        if (arg == 0 || args.length == 0) {
            flag = true;
        } else if (arg == 1) {
            flag = true;
            prefix = args[0];
        }
        if (flag) {
            String finalPrefix = prefix;
            return Client.instance.moduleManager.getModules().stream().filter(mod -> mod.getName().toLowerCase().startsWith(finalPrefix)).map(Module::getName).collect(Collectors.toList());
        }
        if (arg == 2) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add("none");
            arrayList.add("show");
            return arrayList;
        }
        return new ArrayList<String>();
    }

    @Override
    public void run(String[] args) {
        if (args.length == 2) {
            Module m = Client.instance.moduleManager.getModule(args[0]);
            if (m != null) {
                int key = Keyboard.getKeyIndex((String)args[1].toUpperCase());
                m.setKey(key);
                HelperUtil.sendMessage((Object)((Object)EnumChatFormatting.GREEN) + "Success bound " + m.getName() + " to " + Keyboard.getKeyName((int)m.getKey()) + "!");
            } else {
                HelperUtil.sendMessage((Object)((Object)EnumChatFormatting.RED) + "Module not found!");
            }
        } else {
            HelperUtil.sendMessage((Object)((Object)EnumChatFormatting.RED) + "Usage: bind <Module> <Key>");
        }
    }
}

