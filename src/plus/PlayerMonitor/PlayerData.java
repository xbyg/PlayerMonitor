package plus.PlayerMonitor;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.level.Location;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.plugin.Plugin;

public class PlayerData extends MetadataValue {
	private Location location;
	private PlayerInventory inventory;
	private float health;
	private int foodLevel;

	public PlayerData(Plugin plugin, Location location, PlayerInventory inventory, float health, int foodLevel) {
		super(plugin);
		this.location = location;
		this.inventory = inventory;
		this.health = health;
		this.foodLevel = foodLevel;
	}

	@Override
	public void invalidate() {
		System.out.println("invalidate");
	}

	@Override
	public Object value() {
		return this;
	}

	public Location getLocation() {
		return location;
	}

	public PlayerInventory getInventory() {
		return inventory;
	}

	public float getHealth() {
		return health;
	}

	public float getFoodLevel() {
		return foodLevel;
	}

	public void sendData(Player player) {
		player.teleportImmediate(location);
		if (player.isSurvival()) {
			player.setHealth(health);
			player.getFoodData().setLevel(foodLevel);
			player.getInventory().setContents(inventory.getContents());
		}
	}
}
