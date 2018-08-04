package de.riotseb.builderswand.util;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Stairs;

import java.util.Comparator;
import java.util.TreeSet;

@Getter
public class PlayerEditInfo {

	private Location location;
	private BlockDataValue currentlyEditing;
	private TreeSet<BlockDataValue> editableData = Sets.newTreeSet(Comparator.comparingInt(BlockDataValue::getSort));

	public PlayerEditInfo(Location location){
		updateLocationEditableData(location);
		setNextEditingData();
	}

	public void setNextEditingData() {

		if (this.currentlyEditing == null) {
			if (editableData.size() > 0) {
				this.currentlyEditing = editableData.first();
				return;
			}
		} else {
			if (editableData.size() > 1) {
				BlockDataValue next = editableData.higher(this.currentlyEditing);
				if (next == null) {
					next = editableData.first();
				}
				this.currentlyEditing = next;
			}
		}

	}

	public void updateLocationEditableData(Location location) {

		if (this.location != null && !location.equals(this.location)) {
			this.currentlyEditing = null;
		}


		BlockData blockData = location.getBlock().getBlockData();
		editableData.clear();

		if (blockData instanceof Directional) {
			editableData.add(BlockDataValue.DIRECTION);
		}

		if (blockData instanceof Stairs) {
			editableData.add(BlockDataValue.STAIRS);
		}

		if (blockData instanceof Bisected) {
			editableData.add(BlockDataValue.HALF);
		}

		if (blockData instanceof Rotatable) {
			editableData.add(BlockDataValue.ROTATION);
		}

		if (!location.equals(this.location)){
			setNextEditingData();
		}

		this.location = location;

	}

	public enum BlockDataValue {
		DIRECTION(1),
		STAIRS(2),
		HALF(3),
		ROTATION(4);

		@Getter
		private int sort;

		BlockDataValue(int sort) {
			this.sort = sort;
		}
	}

}
