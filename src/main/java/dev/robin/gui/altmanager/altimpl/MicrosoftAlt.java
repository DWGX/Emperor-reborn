package dev.robin.gui.altmanager.altimpl;

import dev.robin.gui.altmanager.AccountEnum;
import dev.robin.gui.altmanager.Alt;

public final class MicrosoftAlt
extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName, String refreshToken) {
        super(userName, AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }
}

