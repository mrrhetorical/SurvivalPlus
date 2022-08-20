package com.rhetorical.survival.items;

import com.rhetorical.survival.SurvivalPlus;
import com.rhetorical.survival.bandages.BandageHandler;
import com.rhetorical.survival.util.Glow;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ItemListener implements Listener {

	private final Material[] soulboundItems = {Material.WOODEN_SHOVEL, Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_SWORD,
		Material.STONE_SHOVEL, Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_HOE, Material.STONE_SWORD,
		Material.IRON_SHOVEL, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_SWORD,
		Material.GOLDEN_SHOVEL, Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_SWORD,
		Material.DIAMOND_SHOVEL, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_SWORD,
		Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET,
		Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET,
		Material.GOLDEN_BOOTS, Material.GOLDEN_LEGGINGS, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_HELMET,
		Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE, Material.IRON_HELMET,
		Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET,
		Material.TURTLE_HELMET, Material.BOW, Material.CROSSBOW, Material.TRIDENT, Material.SHEARS, Material.SHIELD, Material.FISHING_ROD,
		Material.CLOCK, Material.COMPASS, Material.NETHERITE_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE,
			Material.NETHERITE_BOOTS, Material.NETHERITE_LEGGINGS, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_HELMET};

	private Map<World, Set<Player>> sleepingPlayers = new HashMap<>();
	private Map<World, BukkitRunnable> worldRunnableMap = new HashMap<>();

	@EventHandler
	public void onPlayerUseBandage(PlayerInteractEvent e) {
		if (!SurvivalPlus.getInstance().isBandage())
			return;

		if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getAction() == Action.RIGHT_CLICK_BLOCK)
			return;

		Player p = e.getPlayer();

		if (p.getGameMode() != GameMode.SURVIVAL && p.getGameMode() != GameMode.ADVENTURE)
			return;

		ItemStack[] toCheck;

		try {
			toCheck = new ItemStack[2];
			toCheck[0] = (ItemStack) p.getInventory().getClass().getMethod("getItemInMainHand").invoke(p);
			toCheck[1] = (ItemStack) p.getInventory().getClass().getMethod("getItemInOffHand").invoke(p);
		} catch(NoSuchMethodException ex) {
			toCheck = new ItemStack[1];
			toCheck[0] = p.getInventory().getItemInHand();
		} catch(InvocationTargetException ex) {
			toCheck = new ItemStack[1];
			toCheck[0] = p.getInventory().getItemInHand();
		} catch(IllegalAccessException ex) {
			toCheck = new ItemStack[1];
			toCheck[0] = p.getInventory().getItemInHand();
		} catch(IllegalArgumentException ex) {
			toCheck = new ItemStack[1];
			toCheck[0] = p.getInventory().getItemInHand();
		}

		for (ItemStack i : toCheck) {
			if (i.getType().equals(ItemManager.getBandageItem().getType()) && i.getDurability() == ItemManager.getBandageItem().getDurability()) {

				if (i.getAmount() > 1) {
					i.setAmount(i.getAmount() - 1);
					p.updateInventory();
				} else {
					p.getInventory().remove(i);
				}
				new BandageHandler(p);
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerHitWithLance(EntityDamageByEntityEvent e) {
		if (!SurvivalPlus.getInstance().isLance())
			return;

		if (!(e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity))
			return;

		Player p = (Player) e.getDamager();
		LivingEntity damaged = (LivingEntity) e.getEntity();

		if (!p.getInventory().getItemInMainHand().equals(ItemManager.getLanceItem()))
			return;

		e.setCancelled(true);


		if (p.isInsideVehicle()) {
			if (!(p.getVehicle() instanceof LivingEntity))
				return;
		} else {
			return;
		}

		if (damaged instanceof Player) {
			Player damagedPlayer = (Player) damaged;
			if (damagedPlayer.isInsideVehicle()) {
				damagedPlayer.leaveVehicle();
			}
		}

		e.getEntity().setVelocity(p.getLocation().getDirection().multiply(1.9d).setY(0.3d));
		damaged.damage(5d);
	}

	@SuppressWarnings({"ConstantConditions"})
	@EventHandler
	public void onPlayerRightClickCrop(PlayerInteractEvent e) throws NullPointerException {

		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Block b = e.getClickedBlock();

		if (b == null)
			return;

		if (!isHarvestable(b))
			return;


		BlockData bData = b.getBlockData();
		Ageable crop = (Ageable) bData;
		if (crop.getAge() == crop.getMaximumAge()) {
			crop.setAge(0);
			final List<ItemStack> drops = getCropDrops(b);
			for (ItemStack item : drops)
				b.getLocation().getWorld().dropItemNaturally(b.getLocation(), item);
			Location loc = b.getLocation().add(0.5, 0.5, 0.5);
			b.getWorld().spawnParticle(Particle.COMPOSTER, loc, 4);
			b.getWorld().playSound(loc, Sound.ITEM_CROP_PLANT, 1, 0.8f);
		}
		b.setBlockData(crop);
	}

	private BukkitRunnable trySkipNight(World w) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (!sleepingPlayers.containsKey(w) || sleepingPlayers.get(w).isEmpty() || !(w.getFullTime() > 12541 && w.getFullTime() < 23458)) {
					cancel();
					return;
				}

				int sleeping = 0;

				for (Player p : sleepingPlayers.get(w)) {
					if (p.isSleeping() && p.getSleepTicks() >= 100) {
						sleeping++;
					}
				}

				if (((double) sleeping) / ((double) w.getPlayers().size()) >= SurvivalPlus.getInstance().getSleepVotePercent()) {
					Bukkit.getLogger().info("Skipping night");
					w.setTime(0L);
				}
			}
		};
	}

	@EventHandler
	public void onPlayerSleep(PlayerBedEnterEvent e) {
		if (!SurvivalPlus.getInstance().isSleepVote())
			return;

		World w = e.getPlayer().getWorld();
		Set<Player> pls = sleepingPlayers.getOrDefault(w, new HashSet<>());
		pls.add(e.getPlayer());
		sleepingPlayers.put(w, pls);

		if (!worldRunnableMap.containsKey(w)) {
			BukkitRunnable br = trySkipNight(w);
			br.runTaskTimer(SurvivalPlus.getInstance(), 2L, 2L);
			worldRunnableMap.put(w, br);
		}
	}

	@EventHandler
	public void onPlayerStopSleep(PlayerBedLeaveEvent e) {
		if (!SurvivalPlus.getInstance().isSleepVote())
			return;

		if (sleepingPlayers.containsKey(e.getPlayer().getWorld())) {
			Set<Player> pls = sleepingPlayers.getOrDefault(e.getPlayer().getWorld(), new HashSet<>());
			pls.remove(e.getPlayer());
			if (pls.isEmpty()) {
				sleepingPlayers.remove(e.getPlayer().getWorld());
				worldRunnableMap.get(e.getPlayer().getWorld()).cancel();
				worldRunnableMap.remove(e.getPlayer().getWorld());
			} else {
				sleepingPlayers.put(e.getPlayer().getWorld(), pls);
			}
		}
	}

	private List<ItemStack> getCropDrops(Block b) {
		List<ItemStack> stack = new ArrayList<>();
		if(b.getType() == Material.WHEAT) {
			stack.add(new ItemStack(Material.WHEAT, 1));
			int seedCount = (new Random()).nextInt(3);
			if (seedCount != 0)
				stack.add(new ItemStack(Material.WHEAT_SEEDS, seedCount));
		}

		if (b.getType() == Material.CARROTS) {
			int amount = (new Random()).nextInt(4) + 1;
			stack.add(new ItemStack(Material.CARROT, amount));
		}

		if (b.getType() == Material.POTATOES) {
			int amount = (new Random()).nextInt(4) + 1;
			stack.add(new ItemStack(Material.POTATO, amount));
		}

		if (b.getType() == Material.BEETROOTS) {
			stack.add(new ItemStack(Material.BEETROOT, 1));
			int seedCount = (new Random()).nextInt(3);
			if (seedCount != 0)
				stack.add(new ItemStack(Material.BEETROOT_SEEDS, seedCount));
		}

		if (b.getType() == Material.NETHER_WART) {
			int amount = (new Random()).nextInt(3) + 2;
			stack.add(new ItemStack(Material.NETHER_WART, amount));
		}

		return stack;
	}

	private static boolean isHarvestable(Block b) {
		return b.getType() == Material.WHEAT
				|| b.getType() == Material.BEETROOTS
				|| b.getType() == Material.POTATOES
				|| b.getType() == Material.CARROTS
				|| b.getType() == Material.NETHER_WART_BLOCK;
	}

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		if (e.getItem().getItemMeta() != null && e.getItem().getItemMeta().getLore() != null && e.getItem().getItemMeta().getLore().equals(ItemManager.getBreadItem().getItemMeta().getLore())) {
			int level = e.getPlayer().getLevel();
			level += 7;
			e.getPlayer().setLevel(level);
			e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1f);
			e.getPlayer().sendMessage(ChatColor.ITALIC + "Eat up comrade! There may not be more!");
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {

		if (!(e.getEntity() instanceof Player))
			return;

		if (!SurvivalPlus.getInstance().isReciprocalDamage())
			return;

		Player damager;

		if (e.getDamager() instanceof Player) {
			damager = (Player) e.getDamager();
		} else if (e.getDamager() instanceof  Projectile) {
			Projectile projectile = (Projectile) e.getDamager();
			if (!(projectile.getShooter() instanceof Player))
				return;
			damager = (Player) projectile.getShooter();
		} else {
			return;
		}

		Player damaged = (Player) e.getEntity();

		if (SurvivalPlus.getInstance().isKickForPvP())
			if (damaged.getHealth() - (e.getDamage() / 6d) <= 0d) {
				e.setCancelled(true);
				damager.kickPlayer("You have been kicked for killing a player.");
				return;
			}

		damager.damage((3d * e.getDamage()) / 4d);
		e.setDamage(e.getDamage() / 2d);
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {

		if (!SurvivalPlus.getInstance().isModifiedMobSpawning())
			return;

		if (!(e.getEntity() instanceof Creeper))
			return;

		boolean spawn = Math.random() > 0.5f;

		if (spawn)
			return;

		e.setCancelled(true);

		int chance = (int) Math.round(Math.random());

		Class<? extends Entity> entityClass;

		switch(chance) {
			case 0:
				entityClass = Vindicator.class;
				break;
			default:
				entityClass = Rabbit.class;
				break;
		}

		if (e.getLocation().getWorld() == null)
			return;

		Entity entity = e.getLocation().getWorld().spawn(e.getLocation(), entityClass);

		if (entity.getType().equals(EntityType.RABBIT)) {
			Rabbit rabbit = (Rabbit) entity;
			rabbit.setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (!SurvivalPlus.getInstance().isModifiedMobSpawning())
			return;

		if (!(e.getEntity() instanceof Creeper))
			return;

		for (ItemStack item : e.getDrops()) {
			if (item.getType() == Material.GUNPOWDER) {
				item.setAmount((int) Math.round((Math.random() * 6) + 2));
			}
		}
	}

	@EventHandler
	public void onPlayerSoulBind(PlayerInteractEvent e) {
		if (!SurvivalPlus.getInstance().isSoulbound())
			return;

		if (e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		ItemStack stack = e.getItem();
		if (stack == null)
			return;

		Block b = e.getClickedBlock();

		if (b == null)
			return;

		if(b.getType() != Material.EMERALD_BLOCK)
			return;

		if (stack.getItemMeta() == null)
			return;

		ItemMeta iMeta = stack.getItemMeta();

		if (!Arrays.asList(soulboundItems).contains(stack.getType()))
			return;

		if (iMeta.getLore() != null && iMeta.getLore().contains(ChatColor.GRAY + "Soulbound"))
			return;

		iMeta.addEnchant(new Glow(70), 0, true);

		List<String> lore = iMeta.getLore() != null ? iMeta.getLore() : new ArrayList<>();

		lore.add(ChatColor.GRAY + "Soulbound");

		iMeta.setLore(lore);

		stack.setItemMeta(iMeta);

		b.setType(Material.AIR);
		b.getWorld().playSound(b.getLocation().add(0.5, 0.5, 0.5), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
		b.getWorld().spawnParticle(Particle.COMPOSTER, b.getLocation().add(0.5, 0.5, 0.5), 40);
	}

	private Map<Player, List<ItemStack>> soulbound = new HashMap<>();

	@EventHandler
	public void onPlayerDieEvent(PlayerDeathEvent e) {
		if (!SurvivalPlus.getInstance().isSoulbound())
			return;

		for (int i = 0; i < 41; i++) {
			ItemStack item = e.getEntity().getInventory().getItem(i);
			if (item == null)
				continue;
			if (item.getItemMeta() == null)
				continue;
			if (item.getItemMeta().getLore() == null)
				continue;
			if (item.getItemMeta().getLore().contains(ChatColor.GRAY + "Soulbound")) {
				List<ItemStack> items = soulbound.get(e.getEntity());

				if (items == null)
					items = new ArrayList<>();
				items.add(item);

				soulbound.put(e.getEntity(), items);
				e.getDrops().remove(item);
			}
		}

	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
		if (!SurvivalPlus.getInstance().isSoulbound())
			return;

		List<ItemStack> items = new ArrayList<>(soulbound.getOrDefault(e.getPlayer(), new ArrayList<>()));
		for (ItemStack item : items) {
			e.getPlayer().getInventory().addItem(item);
		}

		soulbound.remove(e.getPlayer());
	}

	@EventHandler
	public void onAnvil(PrepareAnvilEvent e) {
		if (!SurvivalPlus.getInstance().isSoulbound())
			return;

		if (e.getResult() == null)
			return;

		ItemStack item = e.getInventory().getContents()[0];
		if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null)
			return;

		if (!item.getItemMeta().getLore().contains(ChatColor.GRAY + "Soulbound"))
			return;

		ItemStack res = e.getResult();
		ItemMeta resMeta = res.getItemMeta();
		if (!resMeta.getDisplayName().startsWith(ChatColor.AQUA + ""))
			resMeta.setDisplayName(ChatColor.AQUA + resMeta.getDisplayName());
		res.setItemMeta(resMeta);
		res.removeEnchantment(new Glow(70));
		res.addUnsafeEnchantment(new Glow(70), 0);
		e.setResult(res);
	}
}