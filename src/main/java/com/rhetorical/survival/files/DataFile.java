package com.rhetorical.survival.files;

import com.rhetorical.survival.SurvivalPlus;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class DataFile {

	private FileConfiguration data;
	private File file;

	private DataFile() {
		Plugin p = SurvivalPlus.getInstance();

		if (!p.getDataFolder().exists())
			p.getDataFolder().mkdir();

		file = new File(p.getDataFolder(), "towns.yml");

		if (!file.exists()) {
			try {
				if (!file.createNewFile())
					throw new Exception("File creation failed!");
			} catch (Exception e) {
				Bukkit.getLogger().severe("Could not create 'towns.yml'!");
				return;
			}
		}

		reloadData();
	}

	public static DataFile open() {
		return new DataFile();
	}

	public void saveData() {
		try {
			data.save(file);
		} catch (Exception e) {
			Bukkit.getLogger().severe("Could not save 'towns.yml'!");
		}
		reloadData();
	}

	public FileConfiguration getData() {
		return data;
	}

	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(file);
	}


}
