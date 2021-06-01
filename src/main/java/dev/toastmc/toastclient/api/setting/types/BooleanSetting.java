package dev.toastmc.toastclient.api.setting.types;

/**
 * Interface representing a boolean value that can be toggled.
 * @author lukflug
 */
public interface BooleanSetting {
    /**
     * Toggle the boolean value.
     */
    public void toggle();

    /**
     * Get the boolean value.
     * @return the value
     */
    public boolean enabled();

}
