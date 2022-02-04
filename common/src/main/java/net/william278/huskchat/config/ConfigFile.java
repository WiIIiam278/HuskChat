package net.william278.huskchat.config;

import java.util.Collection;
import java.util.List;

public abstract class ConfigFile {

    public abstract String getString(String path);

    public abstract String getString(String path, String defaultValue);

    public abstract List<String> getStringList(String path);

    public abstract Integer getInteger(String path);

    public abstract Integer getInteger(String path, int defaultValue);

    public abstract Boolean getBoolean(String path);

    public abstract Boolean getBoolean(String path, boolean defaultValue);

    public abstract Boolean contains(String path);

    public abstract Collection<String> getConfigKeys(String path);

    public abstract Collection<String> getConfigKeys();
}
