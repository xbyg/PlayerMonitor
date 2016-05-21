package plus.PlayerMonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.player.PlayerInvalidMoveEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.plugin.Plugin;
import plus.PlayerMonitor.Listener.SwitchableListener;

public class PlayerMonitor extends SwitchableListener {
	public static HashMap<String, PlayerMonitor> monitors = new HashMap<>();
	private Player target;
	private Player player;
	public LinkedHashMap<String, Boolean> settings;

	public PlayerMonitor(Plugin plugin, ArrayList<Class<? extends Event>> events, Player target, Player player,
			LinkedHashMap<String, Boolean> settings) {
		super(plugin, events, EventPriority.HIGHEST);
		this.player = player;
		this.target = target;
		this.settings = settings;
		player.setMetadata("PlayerData", new PlayerData(plugin, player.getLocation(), player.getInventory(),
				player.getHealth(), player.getFoodData().getLevel()));
		monitors.put(player.getName(), this);
	}

	@Override
	public void startListen() {
		if (settings.get("switchPlayerInventory")) {
			player.getInventory().setContents(target.getInventory().getContents());
			player.getInventory().sendContents(player);
		}
		target.hidePlayer(player);
		if (settings.get("followMoving")) {
			player.hidePlayer(target);
		}
		player.teleportImmediate(target.getLocation());
		super.startListen();
	}

	public void stopMonitor() {
		super.stopListen();
		PlayerData data = (PlayerData) player.getMetadata("PlayerData").get(0).value();
		data.sendData(player);
		target.showPlayer(player);
		player.showPlayer(target);
		monitors.remove(player.getName());
	}
	
	public void switchTarget(Player target){
		target.showPlayer(player);
		this.target = target;
		startListen();
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Player getTarget(){
		return target;
	}

	@Override
	public void execute(Listener listener, Event event) {
		switch (event.getClass().getSimpleName()) {
		case "PlayerMoveEvent":
			Player p = ((PlayerMoveEvent) event).getPlayer();
			if (p.equals(target)) {
				player.sendPosition(p, p.yaw, p.pitch);
			}
			break;
		case "PlayerItemHeldEvent":
			PlayerItemHeldEvent itemHeldEvent = (PlayerItemHeldEvent)event;
			if(itemHeldEvent.getPlayer().equals(target)){
				player.getInventory().setHeldItemIndex(itemHeldEvent.getSlot());
			}
			break;
		case "EntityInventoryChangeEvent":
			EntityInventoryChangeEvent InventoryChangeEvent = (EntityInventoryChangeEvent) event;
			if (InventoryChangeEvent.getEntity().equals(target)) {
				player.getInventory().setItem(InventoryChangeEvent.getSlot(), InventoryChangeEvent.getNewItem());
			}
			break;
		case "InventoryOpenEvent":
			InventoryOpenEvent inventoryOpenEvent = (InventoryOpenEvent) event;
			if(inventoryOpenEvent.getPlayer().equals(target)){
				player.addWindow(inventoryOpenEvent.getInventory());	
			}
			break;
		case "InventoryCloseEvent":
			InventoryCloseEvent inventoryCloseEvent = (InventoryCloseEvent) event;
			if(inventoryCloseEvent.getPlayer().equals(target)){
				player.removeWindow(inventoryCloseEvent.getInventory());
			}
			break;
		case "PlayerInvalidMoveEvent"://当sendPosition的时候,如果监控者卡在方块里会触发InvalidMove
			PlayerInvalidMoveEvent invalidMoveEvent = (PlayerInvalidMoveEvent)event;
			if(invalidMoveEvent.getPlayer().equals(player)){
				event.setCancelled();
			}
			break;
		case "PlayerRespawnEvent":
			PlayerRespawnEvent respawnEvent = (PlayerRespawnEvent)event;
			if(respawnEvent.getPlayer().equals(target)){
				if(settings.get("keepMonitorAfterDeath")){
					if (settings.get("switchPlayerInventory")) {
						player.getInventory().setContents(target.getInventory().getContents());
						player.getInventory().sendContents(player);
					}
					player.teleport(respawnEvent.getRespawnPosition());
				}else{
					stopMonitor();
				}
			}
			break;
		case "PlayerQuitEvent":
			PlayerQuitEvent quitEvent = (PlayerQuitEvent)event;
			if(quitEvent.getPlayer().equals(target) || quitEvent.getPlayer().equals(player)){
				stopMonitor();
			}
			break;
		}
	}
}