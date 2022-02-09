package net.william278.huskchat.config;

import java.util.Collection;
import java.util.List;

public interface ConfigFile {

    String getString(String path);

    String getString(String path, String defaultValue);

    List<String> getStringList(String path);

    Integer getInteger(String path);

    Integer getInteger(String path, int defaultValue);

    Double getDouble(String path);

    Double getDouble(String path, double defaultValue);

    Boolean getBoolean(String path);

    Boolean getBoolean(String path, boolean defaultValue);

    Boolean contains(String path);

    Collection<String> getConfigKeys(String path);

    Collection<String> getConfigKeys();
}
