package plus.PlayerMonitor;

import java.io.File;

import cn.nukkit.plugin.PluginBase;
import plus.PlayerMonitor.Command.MonitorCommand;

public class Main extends PluginBase{
	public void onEnable(){
		getServer().getLogger().info("§b监控玩家插件已加载!");
		File path = new File(getServer().getDataPath()+"/plugins/PlayerMonitor/");
		if(!path.exists()){
			path.mkdirs();
		}
		new MonitorConfig(path+"/Settings.yml");
		this.registerCommands();
	}
	private void registerCommands(){
		this.getServer().getCommandMap().register("PlayerMonitor",new MonitorCommand());
	}
}
