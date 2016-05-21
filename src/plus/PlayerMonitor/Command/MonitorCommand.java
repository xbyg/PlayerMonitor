package plus.PlayerMonitor.Command;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Event;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.player.PlayerInvalidMoveEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import plus.PlayerMonitor.MonitorConfig;
import plus.PlayerMonitor.PlayerMonitor;

public class MonitorCommand extends Command {

	public MonitorCommand() {
		super("monitor");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!(sender instanceof Player) || !sender.isOp()) {
			return false;
		}
		Player player = (Player) sender;
		switch (args[0]) {
		case "start":
			Player target;
			if (!args[1].equals(sender.getName())) {

				if ((target = Server.getInstance().getPlayerExact(args[1])) != null) {
					ArrayList<Class<? extends Event>> events = new ArrayList<>();
					LinkedHashMap<String, Boolean> settings = MonitorConfig.getInstance().getSettings();
					events.add(PlayerRespawnEvent.class);
					events.add(PlayerQuitEvent.class);
					events.add(PlayerInvalidMoveEvent.class);
					if (settings.get("followMoving")) {
						events.add(PlayerMoveEvent.class);
					}
					if (settings.get("monitorInventoryEvent")) {
						events.add(InventoryOpenEvent.class);
						events.add(InventoryCloseEvent.class);
					}
					if (settings.get("switchPlayerInventory")) {
						events.add(PlayerItemHeldEvent.class);
						events.add(EntityInventoryChangeEvent.class);
					}
					new PlayerMonitor(Server.getInstance().getPluginManager().getPlugin("PlayerMonitor"), events,
							target, player, settings).startListen();
					sender.sendMessage("§b[实时监控]§e你正在监控" + target.getName() + "!");
				} else {
					sender.sendMessage("§c玩家不存在!§e请检查输入的玩家名称(" + args[1] + ")是否正确!");
				}
			} else {
				sender.sendMessage("§c无法监控自身");
			}
			break;
		case "stop":
			PlayerMonitor playerMonitor;
			if ((playerMonitor = PlayerMonitor.monitors.get(sender.getName())) != null) {
				playerMonitor.stopMonitor();
				sender.sendMessage("§e[实时监控]§b你取消了监控" + playerMonitor.getTarget().getName() + "玩家!");
			} else {
				sender.sendMessage("§c你并没有在监控任何玩家!");
			}
			break;
		case "switch":
			if ((playerMonitor = PlayerMonitor.monitors.get(sender.getName())) != null) {
				if ((target = Server.getInstance().getPlayerExact(args[1])) != null) {
					playerMonitor.switchTarget(target);
					sender.sendMessage("§e[实时监控]§b你已切换至监控" + playerMonitor.getTarget().getName() + "玩家!");
				} else {
					sender.sendMessage("玩家" + args[1] + "不存在!");
				}
			} else {
				sender.sendMessage("§c你并没有在监控任何玩家!");
			}
			break;
		case "all":
			String msg = "";
			for (PlayerMonitor monitor : PlayerMonitor.monitors.values()) {
				msg += "§b" + monitor.getPlayer().getName() + "§e正在监控§b" + monitor.getTarget().getName() + "§a(IP:"
						+ monitor.getTarget().getAddress() + ")§b玩家\n";
			}
			msg = msg.equals("") ? "§b没有玩家在被监控!" : msg;
			sender.sendMessage(msg);
			break;
		}
		return false;
	}
}
