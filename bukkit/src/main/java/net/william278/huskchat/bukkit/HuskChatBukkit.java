package net.william278.huskchat.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

//todo Hook into plugin messages and handle PlaceholderAPI integration
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
        getLogger().severe("HuskChat needs to be installed on the BungeeCord or Velocity server!");
    }

    @Override
    public void onDisable() {

    }
}
