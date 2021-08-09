package dev.granny.pl3xmapbanners.data;

import dev.granny.pl3xmapbanners.Logger;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.LayerProvider;
import net.pl3x.map.api.marker.Icon;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import dev.granny.pl3xmapbanners.configuration.WorldConfig;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BannerLayerProvider implements LayerProvider {
    private final Map<Position, Data> data = new ConcurrentHashMap<>();
    private final WorldConfig worldConfig;

    public BannerLayerProvider(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    @Override
    public @NonNull String getLabel() {
        return this.worldConfig.LAYER_LABEL;
    }

    @Override
    public boolean showControls() {
        return this.worldConfig.LAYER_CONTROLS;
    }

    @Override
    public boolean defaultHidden() {
        return this.worldConfig.LAYER_CONTROLS_HIDDEN;
    }

    @Override
    public int layerPriority() {
        return this.worldConfig.LAYER_PRIORITY;
    }

    @Override
    public int zIndex() {
        return this.worldConfig.LAYER_ZINDEX;
    }

    @Override
    public @NonNull Collection<Marker> getMarkers() {
        return this.data.values().stream()
                .map(Data::getMarker)
                .collect(Collectors.toSet());
    }

    public Set<Position> getPositions() {
        return this.data.keySet();
    }

    public Map<Position, Data> getData() {
        return this.data;
    }

    public void add(Position position, Key key, String name) {
        name = name == null ? "null" : name;
        Logger.debug(position + " icon: " + key + " name: " + name);
        Icon icon = Marker.icon(position.point(), key, worldConfig.ICON_SIZE);
        icon.markerOptions(MarkerOptions.builder()
                .hoverTooltip(worldConfig.TOOLTIP
                        .replace("{name}", name)));
        this.data.put(position, new Data(icon, key, name));
    }

    public void remove(Position position) {
        Logger.debug("Removing banner from " + position);
        this.data.remove(position);
    }
}
