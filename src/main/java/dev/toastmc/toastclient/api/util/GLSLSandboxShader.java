package dev.toastmc.toastclient.api.util;

import com.mojang.blaze3d.platform.GlStateManager;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;

public class GLSLSandboxShader {
    private final int programId;
    private final int timeUniform;
    private final int mouseUniform;
    private final int resolutionUniform;

    public GLSLSandboxShader(String fragmentShaderLocation) {
        int program = glCreateProgram();

        GlStateManager.glAttachShader(program, createShader(GLSLSandboxShader.class.getResourceAsStream("/assets/toastclient/shaders/passthrough.vsh"), GL_VERTEX_SHADER));
        GlStateManager.glAttachShader(program, createShader(GLSLSandboxShader.class.getResourceAsStream(fragmentShaderLocation), GL_FRAGMENT_SHADER));

        GlStateManager.glLinkProgram(program);

        int linked = GlStateManager.glGetProgrami(program, GL_LINK_STATUS);

        // If linking failed
        if (linked == 0) {
            System.err.println(GlStateManager.glGetProgramInfoLog(program, GlStateManager.glGetProgrami(program, GL_INFO_LOG_LENGTH)));

            throw new IllegalStateException("Shader failed to link");
        }

        this.programId = program;

        // Setup uniforms
        GlStateManager._glUseProgram(program);

        this.timeUniform = GlStateManager._glGetUniformLocation(program, "time");
        this.mouseUniform = GlStateManager._glGetUniformLocation(program, "mouse");
        this.resolutionUniform = GlStateManager._glGetUniformLocation(program, "resolution");

        GlStateManager._glUseProgram(0);
    }

    public void useShader(int width, int height, float mouseX, float mouseY, float time) {
        GlStateManager._glUseProgram(this.programId);

        glUniform2f(this.resolutionUniform, width, height);
        glUniform2f(this.mouseUniform, mouseX / width, 1.0f - mouseY / height);
        glUniform1f(this.timeUniform, time);
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = GlStateManager.glCreateShader(shaderType);

        GlStateManager.glShaderSource(shader, readStreamToString(inputStream));

        GlStateManager.glCompileShader(shader);

        int compiled = GlStateManager.glGetShaderi(shader, GL_COMPILE_STATUS);

        // If compilation failed
        if (compiled == 0) {
            System.err.println(GlStateManager.glGetShaderInfoLog(shader, GlStateManager.glGetShaderi(shader, GL_INFO_LOG_LENGTH)));

            throw new IllegalStateException("Failed to compile shader");
        }

        return shader;
    }

    private List<String> readStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader.lines().collect(Collectors.toList());
    }
}
