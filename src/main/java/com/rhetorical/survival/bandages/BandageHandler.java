package com.rhetorical.survival.bandages;

import com.rhetorical.survival.SurvivalPlus;
import com.rhetorical.survival.items.ItemManager;
import com.rhetorical.survival.util.PacketHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class BandageHandler {

	private static Map<Player, BukkitRunnable> bandagingPlayers = new HashMap<>();

	public BandageHandler(final Player p) {
		if (bandagingPlayers.containsKey(p)) {
			bandagingPlayers.get(p).cancel();
			bandagingPlayers.remove(p);
			p.getInventory().addItem(ItemManager.getBandageItem());
		}

		bandagingPlayers.put(p, new BukkitRunnable() {

			Location position = p.getLocation();
			final int total = 60;
			int progress = 0;

			@Override
			public void cancel() {
				super.cancel();
			}

			@Override
			public void run() {

				boolean cancelled = p.getLocation().distance(position) > 0.75d;

				if (progress > 60 || cancelled) {

					if (cancelled) {
						p.getInventory().addItem(ItemManager.getBandageItem());
					}

					bandagingPlayers.remove(p);
					this.cancel();
				} else if (progress == 60) {
					if (p.getHealth() < 10d) {
						p.setHealth(p.getHealth() + 10d);
					} else {
						p.setHealth(20d);
					}
				}

				int a = Math.round(((float) progress / (float) total) * 10f);
				int b = 10 - a;

				StringBuilder sb = new StringBuilder();

				sb.append(ChatColor.GREEN);
				for (int i = 0; i < a; i++) {
					sb.append("\u25A0");
				}
				sb.append(ChatColor.RED);
				for (int i = 0; i < b; i++) {
					sb.append("\u25A0");
				}

//				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(sb.toString()));
				(new PacketHandler()).sendActionBarMessage(p, sb.toString());
				progress++;
			}
		});

		bandagingPlayers.get(p).runTaskTimer(SurvivalPlus.getPlugin(), 0L, 1L);
	}



	public static boolean isBandaging(Player p) {
		return bandagingPlayers.containsKey(p);
	}
}