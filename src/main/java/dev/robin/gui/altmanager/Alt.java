package dev.robin.gui.altmanager;

import dev.robin.gui.altmanager.AccountEnum;

public abstract class Alt {
    private final String userName;
    private final AccountEnum accountType;

    public Alt(String userName, AccountEnum accountType) {
        this.userName = userName;
        this.accountType = accountType;
    }

    public AccountEnum getAccountType() {
        return this.accountType;
    }

    public String getUserName() {
        return this.userName;
    }
}

