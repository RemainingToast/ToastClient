package dev.toastmc.toastclient.api.setting.types;

/**
 * A setting representing an enumeration.
 *
 * @author lukflug
 */
public interface EnumSetting {

  /** Cycle through the values of the enumeration. */
  public void increment();
}
