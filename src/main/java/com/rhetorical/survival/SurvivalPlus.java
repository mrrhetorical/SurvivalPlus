package com.rhetorical.survival;

import com.rhetorical.survival.items.ItemListener;
import com.rhetorical.survival.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SurvivalPlus extends JavaPlugin {

	private static SurvivalPlus instance;

	private boolean reciprocalDamage = true;
	private boolean modifiedMobSpawning = false;
	private boolean sleepVote = true;
	private double sleepVotePercent = 0.5d;
	private boolean kickForPvP = false;
	private boolean lance = false;
	private boolean bandage = false;
	private boolean soulbound = true;

	@Override
	public void onEnable() {

		if (instance != null)
			return;

		instance = this;

		saveDefaultConfig();
		reloadConfig();

		reciprocalDamage = getConfig().getBoolean("reciprocalDamage", false);
		modifiedMobSpawning = getConfig().getBoolean("modifiedMobSpawning", false);
		sleepVote = getConfig().getBoolean("sleepVote.enabled", true);
		sleepVotePercent = getConfig().getDouble("sleepVote.percent", 0.5d);
		kickForPvP = getConfig().getBoolean("kickForPvP", false);
		lance = getConfig().getBoolean("items.lance", false);
		bandage = getConfig().getBoolean("items.bandage", false);
		soulbound = getConfig().getBoolean("soulbound", true);

		new ItemManager(); //Instantiate new ItemManager.

		Bukkit.getPluginManager().registerEvents(new ItemListener(), getPlugin());
	}

	@Override
	public void onDisable() {}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (label.equalsIgnoreCase("togglePvP")) {
			if (!sender.hasPermission("sp.togglepvp") && !sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
				return true;
			}
			reciprocalDamage = !reciprocalDamage;
			getConfig().set("reciprocalDamage", reciprocalDamage);
			saveConfig();
			reloadConfig();
			sender.sendMessage(ChatColor.GREEN + "Reciprocal Damage is now " + ChatColor.YELLOW + (reciprocalDamage ? "on" : "off") + ChatColor.GREEN + "!");
			return true;
		} else if (label.equalsIgnoreCase("pingl")) {
			if (!sender.hasPermission("sp.pingloc") && !sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
				return true;
			}
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Improper usage! Correct usage: /pingl (player)");
				return true;
			}

			Player player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "No player is on the server with that name!");
				return true;
			}

			sender.sendMessage(String.format("%s%s%s is at: (%s, %s, %s) in world %s", ChatColor.YELLOW, player.getName(), ChatColor.RESET, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getWorld().getName()));
			return true;
		}

		return false;
	}

	public static SurvivalPlus getInstance() {
		return instance;
	}

	public static JavaPlugin getPlugin() {
		return instance;
	}

	public boolean isReciprocalDamage() {
		return reciprocalDamage;
	}

	public boolean isModifiedMobSpawning() {
		return modifiedMobSpawning;
	}

	public boolean isSleepVote() {
		return sleepVote;
	}

	public double getSleepVotePercent() {
		return sleepVotePercent;
	}

	public boolean isKickForPvP() {
		return kickForPvP;
	}

	public boolean isLance() {
		return lance;
	}

	public boolean isBandage() {
		return bandage;
	}

	public boolean isSoulbound() {
		return soulbound;
	}
}