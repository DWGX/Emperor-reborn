package dev.emperor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CryptoUtils {
    public static void encryptString(OutputStream out, String str) throws IOException {
        if (str == null) {
            CryptoUtils.writeInt(out, -1);
        } else if (str.isEmpty()) {
            CryptoUtils.writeInt(out, 0);
        } else {
            byte[] bs = str.getBytes(StandardCharsets.UTF_8);
            CryptoUtils.writeInt(out, bs.length);
            out.write(bs);
        }
    }

    public static void writeInt(OutputStream out, int i) throws IOException {
        out.write((byte)(i >> 24));
        out.write((byte)(i >> 16));
        out.write((byte)(i >> 8));
        out.write(i);
    }

    public static String decryptString(InputStream in) throws IOException {
        int length = CryptoUtils.decrypt(in);
        if (length < 0) {
            return null;
        }
        if (length == 0) {
            return "";
        }
        byte[] value = new byte[length];
        in.read(value);
        return new String(value, StandardCharsets.UTF_8);
    }

    public static void encrypt(OutputStream out, int i) throws IOException {
        out.write((byte)(i >> 24));
        out.write((byte)(i >> 16));
        out.write((byte)(i >> 8));
        out.write(i);
    }

    public static int decrypt(InputStream in) throws IOException {
        return ((byte)in.read() & 0xFF) << 24 | ((byte)in.read() & 0xFF) << 16 | ((byte)in.read() & 0xFF) << 8 | (byte)in.read() & 0xFF;
    }
}

