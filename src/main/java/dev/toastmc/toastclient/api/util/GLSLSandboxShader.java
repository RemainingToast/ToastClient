package dev.toastmc.toastclient.api.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;

public class GLSLSandboxShader {
    private final int programId;
    private final int timeUniform;
    private final int mouseUniform;
    private final int resolutionUniform;

    public GLSLSandboxShader(String fragmentShaderLocation) {
        int program = glCreateProgram();

        glAttachShader(program, createShader(GLSLSandboxShader.class.getResourceAsStream("/assets/toastclient/shaders/passthrough.vsh"), GL_VERTEX_SHADER));
        glAttachShader(program, createShader(GLSLSandboxShader.class.getResourceAsStream(fragmentShaderLocation), GL_FRAGMENT_SHADER));

        glLinkProgram(program);

        int linked = glGetProgrami(program, GL_LINK_STATUS);

        // If linking failed
        if (linked == 0) {
            System.err.println(glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH)));

            throw new IllegalStateException("Shader failed to link");
        }

        this.programId = program;

        // Setup uniforms
        glUseProgram(program);

        this.timeUniform = glGetUniformLocation(program, "time");
        this.mouseUniform = glGetUniformLocation(program, "mouse");
        this.resolutionUniform = glGetUniformLocation(program, "resolution");

        glUseProgram(0);
    }

    public void useShader(int width, int height, float mouseX, float mouseY, float time) {
        glUseProgram(this.programId);

        glUniform2f(this.resolutionUniform, width, height);
        glUniform2f(this.mouseUniform, mouseX / width, 1.0f - mouseY / height);
        glUniform1f(this.timeUniform, time);
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);

        glShaderSource(shader, readStreamToString(inputStream));

        glCompileShader(shader);

        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);

        // If compilation failed
        if (compiled == 0) {
            System.err.println(glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH)));

            throw new IllegalStateException("Failed to compile shader");
        }

        return shader;
    }

    private String readStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader.lines().collect(Collectors.joining("\n"));
    }
}
