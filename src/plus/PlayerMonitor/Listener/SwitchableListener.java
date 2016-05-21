package plus.PlayerMonitor.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nukkit.event.Event;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.EventExecutor;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;

public abstract class SwitchableListener implements Listener, EventExecutor {
	//public static HashMap<Class<? extends SwitchableListener>,SwitchableListener> listeners = new HashMap<>();
	private Plugin plugin;
	public EventPriority priority;
	private List<Class<? extends Event>> events;
	private boolean isListening = false;
	
	public SwitchableListener(Plugin plugin, Class<? extends Event> event) {
		this(plugin, event ,EventPriority.HIGHEST);
	}

	public SwitchableListener(Plugin plugin, Class<? extends Event> event, EventPriority priority) {
		this(plugin,new ArrayList<>(Arrays.asList(event)),priority);
	}

	public SwitchableListener(Plugin plugin,List<Class<? extends Event>> events, EventPriority priority){
		this.plugin = plugin;
		this.events = events;
		this.priority = priority;
		//listeners.put(this.getClass(),this);
	}
	
	public void stopListen() {
		isListening = false;
		HandlerList.unregisterAll(this);
	}

	public void startListen() {
		isListening = true;
		registerEvents();
	}
	
	public boolean isListening(){
		return isListening;
	}
	
	private void registerEvents(){
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		for(Class<? extends Event> event : events){
			pluginManager.registerEvent(event,this,priority,this,plugin);
		}
	}
}
