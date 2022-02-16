package net.william278.huskchat.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

//todo hook into plugin messages
public class HuskChatBukkit extends JavaPlugin {

    private static HuskChatBukkit instance;

    public static HuskChatBukkit getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
