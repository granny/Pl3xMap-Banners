package dev.granny.pl3xmapbanners.hook;

import dev.granny.pl3xmapbanners.data.BannerLayerProvider;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Pl3xMapProvider;
import dev.granny.pl3xmapbanners.configuration.WorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pl3xMapHook {
    private final Map<UUID, BannerLayerProvider> providers = new HashMap<>();

    public Map<UUID, BannerLayerProvider> getProviders() {
        return this.providers;
    }

    public void load() {
        Bukkit.getWorlds().forEach(this::getProvider);
    }

    public BannerLayerProvider getProvider(World world) {
        BannerLayerProvider provider = this.providers.get(world.getUID());
        if (provider != null) {
            return provider;
        }
        MapWorld mapWorld = Pl3xMapProvider.get().getWorldIfEnabled(world.getUID()).orElse(null);
        if (mapWorld == null) {
            return null;
        }
        WorldConfig worldConfig = WorldConfig.get(world);
        if (!worldConfig.ENABLED) {
            return null;
        }
        provider = new BannerLayerProvider(worldConfig);
        Key key = Key.of("banners_" + mapWorld.uuid());
        mapWorld.layerRegistry().register(key, provider);
        this.providers.put(mapWorld.uuid(), provider);
        return provider;
    }

    public void unloadProvider(World world) {
        unloadProvider(world.getUID());
    }

    public void unloadProvider(UUID uuid) {
        Pl3xMapProvider.get().getWorldIfEnabled(uuid).ifPresent(world -> {
            Key key = Key.of("banners_" + world.uuid());
            world.layerRegistry().unregister(key);
        });
    }

    public void disable() {
        this.providers.forEach((uuid, provider) -> unloadProvider(uuid));
    }
}
