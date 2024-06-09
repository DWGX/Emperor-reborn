package dev.robin.command.commands;

import dev.robin.Client;
import dev.robin.command.Command;
import dev.robin.utils.client.HelperUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.EnumChatFormatting;

public class ConfigCommand
extends Command {
    public ConfigCommand() {
        super("config", "cfg");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void run(String[] args) {
        if (args.length == 2) {
            switch (args[0]) {
                case "save": {
                    String name = args[1];
                    if (!name.isEmpty()) {
                        Client.instance.configManager.saveUserConfig(name + ".json");
                        HelperUtil.sendMessage((Object)((Object)EnumChatFormatting.GREEN) + "Config " + name + " Saved!");
                        break;
                    }
                    HelperUtil.sendMessage((Object)((Object)EnumChatFormatting.RED) + "?");
                    break;
                }
                case "load": {
                    String name = args[1];
                    if (!name.isEmpty()) {
                        Client.instance.configManager.loadUserConfig(name + ".json");
                        HelperUtil.sendMessage((Object)((Object)EnumChatFormatting.GREEN) + "Config " + name + " Loaded!");
                        break;
                    }
                    HelperUtil.sendMessage((Object)((Object)EnumChatFormatting.RED) + "?");
                    break;
                }
            }
        } else {
            HelperUtil.sendMessage((Object)((Object)EnumChatFormatting.RED) + "Usage: config save/load <name>");
        }
    }
}

