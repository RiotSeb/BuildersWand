package de.riotseb.builderswand.command;

import de.riotseb.builderswand.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildersWandCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length == 0) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Use: /buildersdebugstick <player>");
				return true;
			}

			Player player = (Player) sender;

			if (!player.hasPermission(Main.DEBUG_STICK_COMMAND_PERMISSION)) {
				player.sendMessage(Main.NO_PERMISSION_MESSAGE);
				return true;
			}

			player.getInventory().addItem(Main.getInstance().getDebugStick());
			sender.sendMessage(Main.DEBUG_STICK_RECEIVED_MESSAGE);
			return true;

		} else {

			if (!sender.hasPermission(Main.DEBUG_STICK_COMMAND_PERMISSION)){
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
				return true;
			}

			Player target = Bukkit.getPlayer(args[0]);

			if (target == null){
				sender.sendMessage(String.format(Main.PLAYER_NOT_ONLINE_MESSAGE, args[0]));
				return true;
			}

			target.getInventory().addItem(Main.getInstance().getDebugStick());

			sender.sendMessage(String.format(Main.OTHER_PLAYER_RECEIVED_STICK_MESSAGE, target.getName()));
			target.sendMessage(Main.DEBUG_STICK_RECEIVED_MESSAGE);
			return true;


		}

	}
}
