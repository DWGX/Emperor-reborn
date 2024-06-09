package dev.emperor.command;

import dev.emperor.command.commands.AvatarCommand;
import dev.emperor.command.commands.BindCommand;
import dev.emperor.command.commands.BindsCommand;
import dev.emperor.command.commands.ConfigCommand;
import dev.emperor.command.commands.HelpCommand;
import dev.emperor.command.commands.ToggleCommand;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {
    @Getter
    private final List<Command> commands = new ArrayList<>();
    public String[] latestAutoComplete = new String[0];
    public String prefix = ".";

    public void init() {
        this.reg(new BindCommand());
        this.reg(new ToggleCommand());
        this.reg(new ConfigCommand());
        this.reg(new BindsCommand());
        this.reg(new HelpCommand());
        this.reg(new AvatarCommand());
    }

    public void autoComplete(String currCmd) {
        Command currentCommand;
        String raw = currCmd.substring(1);
        String[] split = raw.split(" ");
        ArrayList<String> ret = new ArrayList<>();
        Command command = currentCommand = split.length >= 1 ? this.commands.stream().filter(cmd -> cmd.match(split[0])).findFirst().orElse(null) : null;
        if (split.length >= 2 || currentCommand != null && currCmd.endsWith(" ")) {
            String[] stringArray;
            if (currentCommand == null) {
                return;
            }
            String[] args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, split.length - 1);
            ArrayList autocomplete = (ArrayList) currentCommand.autoComplete(args.length + (currCmd.endsWith(" ") ? 1 : 0), args);
            if (!autocomplete.isEmpty() && autocomplete.get(0).equals("none")) {
                String[] stringArray2 = new String[1];
                stringArray = stringArray2;
                stringArray2[0] = "";
            } else {
                stringArray = (String[]) autocomplete.toArray(new String[0]);
            }
            this.latestAutoComplete = stringArray;
            return;
        }
        if (split.length == 1) {
            for (Command command2 : this.commands) {
                ret.addAll(Arrays.asList(command2.getNames()));
            }
            ret.stream().map(str -> "." + str).filter(str -> str.toLowerCase().startsWith(currCmd.toLowerCase())).collect(Collectors.toList());
        }
    }

    public void reg(Command command) {
        this.commands.add(command);
    }

    public Command getCommand(String name) {
        for (Command c : this.commands) {
            for (String s2 : c.getNames()) {
                if (!s2.equals(name)) continue;
                return c;
            }
        }
        return null;
    }
}

