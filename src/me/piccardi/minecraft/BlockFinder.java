package me.piccardi.minecraft;

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

public class BlockFinder extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		super.onEnable();

		getLogger().info("Block Finder ready!");
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	int size = 4;
	Material blockMaterial = Material.DIAMOND_ORE;
	
	
	private Map<Integer, Long> lastUpdates = new HashMap<Integer, Long>();
	private Long getLastUpdate(Player p) {
		Long last = lastUpdates.get(p.getEntityId());
		return (last!=null)?last:0;
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

		Long sec = System.currentTimeMillis() / 1000;
		//System.out.println(sec!=getLastUpdate(pme.getPlayer()));
		if (sec % 2 == 0 && sec>getLastUpdate(pme.getPlayer())) {
			//getLogger().info("Finder check! " + sec);
			Location loc = pme.getPlayer().getLocation();
			Integer counter = 0;
			double minDis = Double.MAX_VALUE;
			for (int x = loc.getBlockX() - size; x < loc.getBlockX() + size; x++) {
				for (int y = loc.getBlockY() - size; y < loc.getBlockY() + size; y++) {
					for (int z = loc.getBlockZ() - size; z < loc.getBlockZ()
							+ size; z++) {
						if (pme.getPlayer().getWorld().getBlockAt(x, y, z)
								.getType() == blockMaterial) {
							counter++;
							double dis = pme.getPlayer().getLocation().distance(new Location(pme.getPlayer().getWorld(), x, y, z));
							if (minDis>dis)minDis = dis;
						}
					}
				}
			}

			if (counter > 0) {
				pme.getPlayer().sendMessage(counter.toString()+ " | Closest "+blockMaterial.name()+": "+minDis);

			}
			
			setLastUpdate(pme.getPlayer(), sec);
		}
	}
}
