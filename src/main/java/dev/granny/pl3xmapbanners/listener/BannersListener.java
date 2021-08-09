package dev.granny.pl3xmapbanners.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.granny.pl3xmapbanners.BannersPlugin;
import dev.granny.pl3xmapbanners.Logger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class BannersListener implements Listener {
    private final BannersPlugin plugin;
    private final NamespacedKey key;

    public BannersListener(BannersPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "display_name");
    }

    @EventHandler
    public void onClickBanner(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        BlockState state = block.getState();
        if (!(state instanceof Banner banner)) {
            return;
        }
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.FILLED_MAP) {
            return;
        }
        if (!event.getPlayer().hasPermission("pl3xmap.banners.admin")) {
            return;
        }
        event.setCancelled(true);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            plugin.getBannerManager().removeBanner(state);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Logger.debug("Putting banner: " + banner);
            String displayName = banner.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            Logger.debug("key: " + key + " PersistentDataContainer: " + displayName);
            plugin.getBannerManager().putBanner(state, displayName);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        BlockState state = event.getBlockPlaced().getState();
        if (state instanceof Banner banner) {
            ItemStack item = event.getItemInHand();
            String itemName = item.hasDisplayName() ? item.getDisplayName() : "";
            Logger.debug("Setting '" + itemName + "' into banner.");
            banner.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemName);
            boolean updateState = banner.update();
            Logger.debug("Update State: " + (updateState ? "Success" : "Failure"));
            Logger.debug("Banner: " + banner);
            Logger.debug("PersistentDataContainer: " + banner.getPersistentDataContainer().get(key, PersistentDataType.STRING));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBannerBreak(BlockDropItemEvent event) {
        remove(event.getBlockState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBannerBreak(BlockDestroyEvent event) {
        remove(event.getBlock().getState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBannerBreak(BlockBurnEvent event) {
        remove(event.getBlock().getState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBannerBreak(BlockExplodeEvent event) {
        event.blockList().forEach(block -> remove(block.getState()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBannerBreak(EntityExplodeEvent event) {
        event.blockList().forEach(block -> remove(block.getState()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBannerBreak(BlockPistonExtendEvent event) {
        event.getBlocks().forEach(block -> remove(block.getState()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBannerBreak(BlockPistonRetractEvent event) {
        event.getBlocks().forEach(block -> remove(block.getState()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBannerBreak(BlockFromToEvent event) {
        remove(event.getToBlock().getState());
    }

    private void remove(BlockState state) {
        if (state instanceof Banner) {
            plugin.getBannerManager().removeBanner(state);
        }
    }
}
