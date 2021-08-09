package dev.granny.pl3xmapbanners;

import dev.granny.pl3xmapbanners.configuration.Config;
import dev.granny.pl3xmapbanners.data.Icons;
import dev.granny.pl3xmapbanners.data.BannersManager;
import dev.granny.pl3xmapbanners.hook.Pl3xMapHook;
import dev.granny.pl3xmapbanners.listener.BannersListener;
import dev.granny.pl3xmapbanners.listener.WorldListener;
import net.pl3x.map.api.Key;
import org.bukkit.plugin.java.JavaPlugin;

public final class BannersPlugin extends JavaPlugin {
    private static BannersPlugin instance;
    private Pl3xMapHook pl3xmapHook;
    private BannersManager bannerManager;

    public BannersPlugin() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Config.reload();

        if (!getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            Logger.severe("Pl3xMap not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //noinspection unused
        Key loadme = Icons.WHITE;

        pl3xmapHook = new Pl3xMapHook();
        pl3xmapHook.load();

        bannerManager = new BannersManager(this);
        bannerManager.load();

        getServer().getPluginManager().registerEvents(new BannersListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
    }

    @Override
    public void onDisable() {
        if (bannerManager != null) {
            bannerManager.save();
            bannerManager = null;
        }

        if (pl3xmapHook != null) {
            pl3xmapHook.disable();
            bannerManager = null;
        }
    }

    public static BannersPlugin getInstance() {
        return instance;
    }

    public Pl3xMapHook getPl3xmapHook() {
        return pl3xmapHook;
    }

    public BannersManager getBannerManager() {
        return bannerManager;
    }
}
