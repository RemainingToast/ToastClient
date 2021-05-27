package dev.toastmc.toastclient.api.setting.types;

import net.minecraft.client.util.InputUtil;

public interface KeySetting {

    public InputUtil.Key getKey();

    public String getKeyName();

    public void setKey(InputUtil.Key key);

}
