package de.riotseb.builderswand;

import de.riotseb.builderswand.command.BuildersWandCommand;
import de.riotseb.builderswand.listener.PlayerListener;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Main extends JavaPlugin {

	public static final String USE_PERMISSION = "builderswand.use";
	public static final String DEBUG_STICK_COMMAND_PERMISSION = "builderswand.get";

	public static final String NO_PERMISSION_MESSAGE = ChatColor.RED + "Not enough permissions.";
	public static final String DEBUG_STICK_RECEIVED_MESSAGE = ChatColor.GREEN + "You received the builders wand.";
	public static final String OTHER_PLAYER_RECEIVED_STICK_MESSAGE = ChatColor.GREEN + "%s received the builders wand.";
	public static final String PLAYER_NOT_ONLINE_MESSAGE = ChatColor.WHITE + "%s" + ChatColor.RED + " is not online.";
	public static final String BLOCK_NOT_EDITABLE_MESSAGE = ChatColor.RED + "That block is not editable";
	public static final String CURRENTLY_EDITING_MESSAGE = ChatColor.GREEN + "Now editing: " + ChatColor.GOLD + "%s";

	@Getter
	private ItemStack debugStick;

	@Getter
	private static Main instance;

	@Override
	public void onEnable() {
		instance = this;

		ItemStack debugStick = new ItemStack(Material.STICK);
		ItemMeta stickMeta = debugStick.getItemMeta();
		stickMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Builders wand");
		stickMeta.setLore(Arrays.asList(ChatColor.GREEN + "Rightclick to change current direction",
				ChatColor.GREEN + "Leftclick to show current direction",
				ChatColor.GREEN + "Shift-Leftclick to change current direction type"));
		debugStick.setItemMeta(stickMeta);
		debugStick.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
		this.debugStick = debugStick;

		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

		getCommand("builderswand").setExecutor(new BuildersWandCommand());

	}

	@Override
	public void onDisable() {

	}

}
