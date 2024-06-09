package dev.emperor.utils.client.menu.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.lwjgl.opengl.GL20;

public class MainMenuBackground {
    private final int programId;
    private final int timeUniform;
    private final int mouseUniform;
    private final int resolutionUniform;

    public MainMenuBackground(String fragmentShaderLocation) throws IOException {
        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, createShader("/assets/minecraft/express/shader/passthrough.vsh", GL20.GL_VERTEX_SHADER));
        GL20.glAttachShader(program, createShader(fragmentShaderLocation, GL20.GL_FRAGMENT_SHADER));
        GL20.glLinkProgram(program);
        int linked = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
        if (linked == 0) {
            System.err.println(GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH)));
            throw new IllegalStateException("Shader program failed to link");
        }
        this.programId = program;
        this.timeUniform = GL20.glGetUniformLocation(program, "time");
        this.mouseUniform = GL20.glGetUniformLocation(program, "mouse");
        this.resolutionUniform = GL20.glGetUniformLocation(program, "resolution");
    }

    public void useShader(int width, int height, float time) {
        GL20.glUseProgram(this.programId);
        GL20.glUniform2f(this.resolutionUniform, (float)width, (float)height);
        GL20.glUniform2f(this.mouseUniform, (float)width, 1.0f - (float)height);
        GL20.glUniform1f(this.timeUniform, time);
    }

    private int createShader(String shaderLocation, int shaderType) throws IOException {
        int shader = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shader, readShaderSource(shaderLocation));
        GL20.glCompileShader(shader);
        int compiled = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
        if (compiled == 0) {
            System.err.println(GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH)));
            throw new IllegalStateException("Failed to compile shader: " + shaderLocation);
        }
        return shader;
    }

    private String readShaderSource(String shaderLocation) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(shaderLocation);
        if (inputStream == null) {
            throw new IOException("Shader file not found: " + shaderLocation);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
