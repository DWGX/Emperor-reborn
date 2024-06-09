package dev.robin.command;

import java.util.List;

public abstract class Command {
    private final String[] name;

    public Command(String ... name) {
        this.name = name;
    }

    public abstract List<String> autoComplete(int var1, String[] var2);

    public String[] getNames() {
        return this.name;
    }

    public abstract void run(String[] var1);

    boolean match(String name) {
        for (String alias : this.name) {
            if (!alias.equalsIgnoreCase(name)) continue;
            return true;
        }
        return this.name[0].equalsIgnoreCase(name);
    }
}

