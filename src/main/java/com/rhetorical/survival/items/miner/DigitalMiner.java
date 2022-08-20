package com.rhetorical.survival.items.miner;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DigitalMiner {

	private Inventory menu;
	private Inventory storage;
	private Inventory fuel;
	private Inventory filters;

	private List<Block> blockList = new ArrayList<>();
	private Block headBlock;

	/* Inventory */
	private List<ItemStack> storageList = new ArrayList<>(),
			fuelList = new ArrayList<>();

	private Map<ItemStack, Boolean> oreFilter = new HashMap<>();

	private boolean enabled = false,
			silkTouch = false;


	public DigitalMiner() {

	}

	public Inventory getMenu() {
		return menu;
	}

	private void setMenu(Inventory value) {
		menu = value;
	}

	public Inventory getStorage() {
		return storage;
	}

	private void setStorage(Inventory value) {
		storage = value;
	}

	public Inventory getFuel() {
		return fuel;
	}

	private void setFuel(Inventory value) {
		fuel = value;
	}

	public Inventory getFilters() {
		return filters;
	}

	private void setFilters(Inventory value) {
		filters = value;
	}

	public Map<ItemStack, Boolean> getOreFilter() {
		return oreFilter;
	}
}
