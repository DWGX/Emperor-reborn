package dev.emperor.command.commands;

import dev.emperor.command.Command;
import java.util.ArrayList;
import java.util.List;

public class AvatarCommand
extends Command {
    public AvatarCommand() {
        super("avatar");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void run(String[] args) {
    }
}

