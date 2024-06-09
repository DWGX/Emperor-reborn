package net.minecraft.client.stream;

import net.minecraft.util.MathHelper;

public class TwitchStream {
    public static int formatStreamFps(float p_152948_0_) {
        return MathHelper.floor_float(10.0f + p_152948_0_ * 50.0f);
    }

    public static int formatStreamKbps(float p_152946_0_) {
        return MathHelper.floor_float(230.0f + p_152946_0_ * 3270.0f);
    }

    public static float formatStreamBps(float p_152947_0_) {
        return 0.1f + p_152947_0_ * 0.1f;
    }
}

