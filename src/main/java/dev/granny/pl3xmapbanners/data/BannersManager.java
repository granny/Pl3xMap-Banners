package dev.granny.pl3xmapbanners.data;

import net.pl3x.map.api.Key;
import dev.granny.pl3xmapbanners.Logger;
import dev.granny.pl3xmapbanners.BannersPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class BannersManager {
    private final BannersPlugin plugin;
    private final File dataDir;

    public BannersManager(BannersPlugin plugin) {
        this.plugin = plugin;
        this.dataDir = new File(plugin.getDataFolder(), "data");

        if (!this.dataDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            this.dataDir.mkdirs();
        }
    }

    public void load() {
        plugin.getPl3xmapHook().getProviders().forEach((uuid, provider) -> {
            YamlConfiguration config = new YamlConfiguration();
            try {
                File file = new File(dataDir, uuid + ".yml");
                if (file.exists()) {
                    Logger.debug("Loading " + uuid + ".yml");
                    config.load(file);
                }
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            config.getKeys(false).forEach(entry -> {
                try {
                    Logger.debug("Entry: " + entry);
                    String[] split = entry.split(",");
                    int x = Integer.parseInt(split[0]);
                    int y = Integer.parseInt(split[1]);
                    int z = Integer.parseInt(split[2]);
                    Position pos = Position.of(x, y, z);
                    Key key = Key.of(Objects.requireNonNull(config.getString(entry + ".key")));
                    String name = config.getString(entry + ".name", "Unknown");
                    provider.add(pos, key, name);
                } catch (Exception e) {
                    Logger.log().log(Level.SEVERE, "Could not load " + entry + " from " + uuid, e);
                }
            });
        });
    }

    public void save() {
        plugin.getPl3xmapHook().getProviders().forEach((uuid, provider) -> {
            YamlConfiguration config = new YamlConfiguration();
            provider.getData().forEach((pos, data) -> {
                String entry = pos.getX() + "," + pos.getY() + "," + pos.getZ();
                Logger.debug("Entry: " + entry + " key: " + data.getKey().getKey() + " name: " + data.getName());
                config.set(entry + ".key", data.getKey().getKey());
                config.set(entry + ".name", data.getName());
            });
            try {
                Logger.debug("Saving data/" + uuid + ".yml ...");
                config.save(new File(new File(plugin.getDataFolder(), "data"), uuid + ".yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeBanner(BlockState state) {
        BannerLayerProvider provider = plugin.getPl3xmapHook().getProvider(state.getWorld());
        if (provider == null) {
            return;
        }
        Location loc = state.getLocation();
        provider.remove(Position.of(loc));
    }

    public void putBanner(BlockState state, String name) {
        BannerLayerProvider provider = plugin.getPl3xmapHook().getProvider(state.getWorld());
        if (provider == null) {
            return;
        }
        provider.add(Position.of(state.getLocation()), Icons.getIcon(state.getType()), name);
    }

    public void checkChunk(Chunk chunk) {
        int minX = chunk.getX();
        int minZ = chunk.getZ();
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        BannerLayerProvider provider = plugin.getPl3xmapHook().getProvider(chunk.getWorld());
        if (provider != null) {
            provider.getPositions().forEach(pos -> {
                if (pos.getX() >= minX && pos.getZ() >= minZ &&
                        pos.getX() <= maxX && pos.getZ() <= maxZ &&
                        !pos.isBanner(chunk.getWorld())) {
                    provider.remove(pos);
                }
            });
        }
    }
}
