package com.rhetorical.survival.items;

import com.rhetorical.survival.SurvivalPlus;
import com.rhetorical.survival.util.Glow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

	private static ItemManager instance;

	private ItemStack bandageItem;
	private ItemStack lance;
	private ItemStack commieBread;

	public ItemManager() {
		if (instance != null)
			return;

		instance = this;

		Glow.registerGlow();
		setupItems();
		setupRecipes();
	}

	public static ItemManager getInstance() {
		return instance;
	}

	public static ItemStack getBandageItem() {
		return getInstance().bandageItem;
	}

	public static ItemStack getLanceItem() {
		return getInstance().lance;
	}

	public static ItemStack getBreadItem() {
		return getInstance().commieBread;
	}

	private void setupItems() {

		Glow glow = new Glow(70);

		Material bandageMaterial;
		short bandageData = (short) SurvivalPlus.getPlugin().getConfig().getInt("Bandage.Data");
		int amount = SurvivalPlus.getPlugin().getConfig().getInt("Bandage.Amount");
		try {
			bandageMaterial = Material.valueOf(SurvivalPlus.getPlugin().getConfig().getString("Bandage.Material"));
		} catch(Exception e) {
			bandageMaterial = Material.PAPER;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not load BandageMaterial! Try looking at the Material Enum!");
		}
//		bandageItem = new ItemStack(bandageMaterial, amount, bandageData);
		bandageItem = new ItemStack(bandageMaterial, amount);
		{
			ItemMeta itemMeta = bandageItem.getItemMeta();
			itemMeta.setDisplayName(ChatColor.RESET + "Bandage");
			itemMeta.setCustomModelData(19);
			bandageItem.setItemMeta(itemMeta);
		}

//		lance = new ItemStack(Material.STICK, 1, (short) 12);
		lance = new ItemStack(Material.STICK, 1);
		ItemMeta lanceMeta = lance.getItemMeta();
		lanceMeta.setDisplayName(ChatColor.RESET + "Lance");
		lanceMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lanceMeta.setCustomModelData(24);
		lance.setItemMeta(lanceMeta);

//		commieBread = new ItemStack(Material.BREAD, 1, (short) 12);
		commieBread = new ItemStack(Material.BREAD, 1);
		ItemMeta breadMeta = commieBread.getItemMeta();
		breadMeta.setDisplayName("" + ChatColor.AQUA + ChatColor.ITALIC + "Communist Bread");
		List<String> breadLore = new ArrayList<>();
		breadLore.add(ChatColor.ITALIC + "Something that's so rare, it");
		breadLore.add(ChatColor.ITALIC + "almost doesn't exist.");
		breadMeta.setLore(breadLore);
		breadMeta.setCustomModelData(13);
		breadMeta.addEnchant(glow, 1, true);
		commieBread.setItemMeta(breadMeta);
	}

	private void setupRecipes() {
		if (SurvivalPlus.getInstance().isBandage()) {
			ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "bandage"), bandageItem);
			recipe.addIngredient(3, Material.PAPER);
			Bukkit.addRecipe(recipe);
		}

		{
			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "saddle"), new ItemStack(Material.SADDLE, 1));
			recipe.shape(" L ", "L L", "I I");
			recipe.setIngredient('L', Material.LEATHER);
			recipe.setIngredient('I', Material.IRON_NUGGET);
			Bukkit.addRecipe(recipe);
		}

		if (SurvivalPlus.getInstance().isLance()) {
			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "lance"), lance);
			recipe.shape("  I", " F ", "F  ");
			recipe.setIngredient('I', Material.IRON_INGOT);
			recipe.setIngredient('F', Material.OAK_FENCE);
			Bukkit.addRecipe(recipe);
		}

		{
			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "xpbottle"), new ItemStack(Material.EXPERIENCE_BOTTLE));
			recipe.shape("RGR", "GBG", "RGR");
			recipe.setIngredient('R', Material.REDSTONE);
			recipe.setIngredient('G', Material.GOLD_NUGGET);
			recipe.setIngredient('B', Material.GLASS_BOTTLE);
			Bukkit.addRecipe(recipe);
		}

		{
			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "communistbread"), commieBread);
			recipe.shape("GOG", "OBO", "GOG");
			recipe.setIngredient('O', Material.OBSIDIAN);
			recipe.setIngredient('G', Material.GOLD_INGOT);
			recipe.setIngredient('B', Material.BREAD);
			Bukkit.addRecipe(recipe);
		}

		{
			ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "book"), new ItemStack(Material.BOOK, 1));
			recipe.addIngredient(4, Material.STRING);
			recipe.addIngredient(3, Material.PAPER);
			Bukkit.addRecipe(recipe);
		}

		{
			ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "string"), new ItemStack(Material.STRING, 4));
			recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.WHITE_WOOL, Material.BLACK_WOOL, Material.BLUE_WOOL, Material.BROWN_WOOL, Material.CYAN_WOOL, Material.GRAY_WOOL, Material.GREEN_WOOL, Material.LIGHT_BLUE_WOOL, Material.LIGHT_GRAY_WOOL, Material.LIME_WOOL, Material.MAGENTA_WOOL, Material.ORANGE_WOOL, Material.PINK_WOOL, Material.PURPLE_WOOL, Material.RED_WOOL, Material.YELLOW_WOOL));
			Bukkit.addRecipe(recipe);
		}

		{
			FurnaceRecipe recipe = new FurnaceRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "leather"), new ItemStack(Material.LEATHER, 1), new RecipeChoice.MaterialChoice(Material.ROTTEN_FLESH), 0.1f, 160);
			Bukkit.addRecipe(recipe);
		}

		{
			ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(SurvivalPlus.getPlugin(), "porkchop"), new ItemStack(Material.PORKCHOP, 1));
			recipe.addIngredient(Material.ROTTEN_FLESH);
			recipe.addIngredient(2, Material.SUGAR);
			Bukkit.addRecipe(recipe);
		}
	}
}