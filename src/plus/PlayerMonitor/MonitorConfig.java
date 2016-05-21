package plus.PlayerMonitor;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import cn.nukkit.utils.Config;

public class MonitorConfig extends Config {
	private static MonitorConfig instance;
	private LinkedHashMap<String, Boolean> settings = new LinkedHashMap<>();

	public MonitorConfig(String path) {
		super(path, Config.YAML);
		if (get("followMoving") == null) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			map.put("followMoving", true);
			map.put("monitorInventoryEvent", true);
			map.put("switchPlayerInventory", true);
			map.put("keepMonitorAfterDeath",true);
			setAll(map);
			save();
		}
		reloadSettings();
		instance = this;
	}

	public static MonitorConfig getInstance() {
		return instance;
	}

	public boolean getSetting(String type) {
		return settings.get(type);
	}

	public LinkedHashMap<String, Boolean> getSettings() {
		return settings;
	}

	public void reloadSettings() {
		LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();
		for (Entry<String, Object> entry : getAll().entrySet()) {
			map.put(entry.getKey(), getBoolean(entry.getKey()));
		}
		this.settings = map;
	}
}
