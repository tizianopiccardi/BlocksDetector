package me.piccardi.bukkit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockDetector extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		super.onEnable();

		getLogger().info("BlockDetector ready!");
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	int distance = 4;
	int interval = 2;
	Material blockMaterial = Material.DIAMOND_ORE;

	private Map<Integer, Long> lastUpdates = new HashMap<Integer, Long>();

	private Long getLastUpdate(Player p) {
		Long last = lastUpdates.get(p.getEntityId());
		return (last != null) ? last : 0;
	}

	private void setLastUpdate(Player p, Long value) {
		lastUpdates.put(p.getEntityId(), value);
	}

	@EventHandler
	public void removeLastUpdate(PlayerQuitEvent pqe) {
		lastUpdates.remove(pqe.getPlayer().getEntityId());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void checkDiamonds(PlayerMoveEvent pme) {

		Player player = pme.getPlayer();

		Long sec = System.currentTimeMillis() / 1000;

		if (sec % interval == 0 && sec > getLastUpdate(pme.getPlayer())) {

			Location loc = player.getLocation();
			Integer counter = 0;
			double minDis = Double.MAX_VALUE;
			for (int x = loc.getBlockX() - distance; x < loc.getBlockX()
					+ distance; x++) {
				for (int y = loc.getBlockY() - distance; y < loc.getBlockY()
						+ distance; y++) {
					for (int z = loc.getBlockZ() - distance; z < loc
							.getBlockZ() + distance; z++) {
						if (player.getWorld().getBlockAt(x, y, z).getType() == blockMaterial) {
							counter++;
							double dis = player.getLocation().distance(
									new Location(player.getWorld(), x, y, z));
							if (minDis > dis)
								minDis = dis;
						}
					}
				}
			}

			if (counter > 0) {
				player.sendMessage("#" + counter.toString() + " | Closest "
						+ blockMaterial.name() + ": " + minDis);

			}

			setLastUpdate(player, sec);
		}
	}
}
