package dev.robin.command.commands;

import dev.robin.Client;
import dev.robin.command.Command;
import dev.robin.utils.DebugUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommand
extends Command {
    public HelpCommand() {
        super("help", "h");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void run(String[] args) {
        DebugUtil.log("\u00a7a[Commands]:\u00a7f");
        for (Command command : Client.instance.commandManager.getCommands()) {
            DebugUtil.log("\u00a7e." + Arrays.toString(command.getNames()));
        }
    }
}

