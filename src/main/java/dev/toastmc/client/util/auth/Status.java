package dev.toastmc.client.util.auth;

import net.minecraft.client.resource.language.I18n;

public enum Status
{
    VALID("\u2717", 0xFF00FF00),
    INVALID("\u2717", 0xFFFF0000),
    UNKNOWN("\u2026", 0xFF999999);

    public final String langKey;
    public final int color;

    Status(String langKey, int color)
    {
        this.langKey = langKey;
        this.color = color;
    }

    @Override
    public String toString()
    {
        return langKey;
    }
}
