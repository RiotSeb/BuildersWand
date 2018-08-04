package de.riotseb.builderswand.listener;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.riotseb.builderswand.Main;
import de.riotseb.builderswand.util.PlayerEditInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerListener implements Listener {

	private Map<UUID, PlayerEditInfo> playerToEditInfo = Maps.newHashMap();

	private static TreeSet<BlockFace> facingSet = Sets.newTreeSet(Arrays.asList(
			BlockFace.EAST,
			BlockFace.NORTH,
			BlockFace.WEST,
			BlockFace.SOUTH
			)
	);

	private static TreeSet<Stairs.Shape> shapeSet = Sets.newTreeSet(Arrays.asList(
			Stairs.Shape.STRAIGHT,
			Stairs.Shape.INNER_LEFT,
			Stairs.Shape.INNER_RIGHT,
			Stairs.Shape.OUTER_LEFT,
			Stairs.Shape.OUTER_RIGHT
			)
	);

	private static TreeSet<BlockFace> rotationSet = Sets.newTreeSet(Arrays.asList(
			BlockFace.NORTH,
			BlockFace.NORTH_NORTH_EAST,
			BlockFace.NORTH_EAST,
			BlockFace.EAST_NORTH_EAST,
			BlockFace.EAST,
			BlockFace.EAST_SOUTH_EAST,
			BlockFace.SOUTH_EAST,
			BlockFace.SOUTH_SOUTH_EAST,
			BlockFace.SOUTH,
			BlockFace.SOUTH_SOUTH_WEST,
			BlockFace.SOUTH_WEST,
			BlockFace.WEST_SOUTH_WEST,
			BlockFace.WEST,
			BlockFace.WEST_NORTH_WEST,
			BlockFace.NORTH_WEST,
			BlockFace.NORTH_NORTH_WEST
			)
	);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		boolean hasPermission = player.hasPermission(Main.USE_PERMISSION);
		ItemStack itemInHand = event.getItem();

		if (itemInHand == null) {
			return;
		}


		if (!Main.getInstance().getDebugStick().isSimilar(itemInHand)) {
			return;
		}

		if (!hasPermission) {
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR
				|| event.getAction() == Action.PHYSICAL) {
			return;
		}

		Block clickedBlock = event.getClickedBlock();
		BlockState blockState = clickedBlock.getState();
		BlockData blockData = blockState.getBlockData();

		event.setCancelled(true);

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

			if (player.isSneaking()) {

				PlayerEditInfo playerEditInfo = playerToEditInfo.getOrDefault(player.getUniqueId(),
						new PlayerEditInfo(clickedBlock.getLocation()));

				playerEditInfo.updateLocationEditableData(clickedBlock.getLocation());
				playerEditInfo.setNextEditingData();
				String message;

				if (playerEditInfo.getCurrentlyEditing() == null) {
					message = Main.BLOCK_NOT_EDITABLE_MESSAGE;
				} else {
					message = String.format(Main.CURRENTLY_EDITING_MESSAGE, playerEditInfo.getCurrentlyEditing().name());
				}

				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
				playerToEditInfo.put(player.getUniqueId(), playerEditInfo);
				return;
			}

			String message = "";

			if (blockData instanceof Directional) {
				message += ChatColor.GREEN + "Facing: " + ChatColor.GOLD + ((Directional) blockData).getFacing();
			}

			if (blockData instanceof Stairs) {
				message += ChatColor.GREEN + " Shape: " + ChatColor.GOLD + ((Stairs) blockData).getShape();
			}

			if (blockData instanceof Bisected) {
				message += ChatColor.GREEN + " Section: " + ChatColor.GOLD + ((Bisected) blockData).getHalf();
			}

			if (blockData instanceof Rotatable) {
				message += ChatColor.GREEN + " Rotation: " + ChatColor.GOLD + ((Rotatable) blockData).getRotation();
			}

			if (message.isEmpty()) {
				message = Main.BLOCK_NOT_EDITABLE_MESSAGE;
			}

			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			PlayerEditInfo info = playerToEditInfo.getOrDefault(player.getUniqueId(), new PlayerEditInfo(clickedBlock.getLocation()));
			info.updateLocationEditableData(clickedBlock.getLocation());
			PlayerEditInfo.BlockDataValue currentlyEditing = info.getCurrentlyEditing();

			if (currentlyEditing == null) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Main.BLOCK_NOT_EDITABLE_MESSAGE));
				return;
			}

			if (currentlyEditing == PlayerEditInfo.BlockDataValue.DIRECTION && (blockData instanceof Directional)) {

				BlockFace next = facingSet.higher(((Directional) blockData).getFacing());
				if (next == null) {
					next = facingSet.first();
				}

				((Directional) blockData).setFacing(next);
				clickedBlock.setBlockData(blockData);
				return;
			}

			if (currentlyEditing == PlayerEditInfo.BlockDataValue.HALF && (blockData instanceof Bisected)) {
				Bisected.Half other = ((Bisected) blockData).getHalf() == Bisected.Half.TOP ? Bisected.Half.BOTTOM : Bisected.Half.TOP;
				((Bisected) blockData).setHalf(other);
				clickedBlock.setBlockData(blockData);
				return;
			}

			if (currentlyEditing == PlayerEditInfo.BlockDataValue.STAIRS && (blockData instanceof Stairs)) {

				Stairs.Shape next = shapeSet.higher(((Stairs) blockData).getShape());
				if (next == null) {
					next = shapeSet.first();
				}

				((Stairs) blockData).setShape(next);
				clickedBlock.setBlockData(blockData);
			}

			if (currentlyEditing == PlayerEditInfo.BlockDataValue.ROTATION && (blockData instanceof Rotatable)) {

				BlockFace next = rotationSet.higher(((Rotatable) blockData).getRotation());
				if (next == null) {
					next = rotationSet.first();
				}

				((Rotatable) blockData).setRotation(next);
				clickedBlock.setBlockData(blockData);
			}

		}

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.playerToEditInfo.remove(event.getPlayer().getUniqueId());
	}

}
