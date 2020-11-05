package xyz.powerthecoder.ManHuntCompass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	
	// Create Cooldown Map
	Map<String, Long> cooldowns = new HashMap<String, Long>();
	
	// Globaly Saved Arguments
	private static String savedArgs;
	public static String getArgs() {
		return savedArgs;
	}
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this,this);
	}
	@Override
	public void onDisable() {
		this.getServer().getPluginManager().registerEvents(this,this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("track")) {
			if (!(sender instanceof Player)) {
				// sender is Console
				sender.sendMessage("You need to be in game");
				return true;
			}
			Player player = (Player) sender;
			if (player.getInventory().firstEmpty() == -1) {
				// Inventory Is Full
				player.sendMessage("You inv is full");
				return true;
			}
			// Give Compass
			player.getInventory().addItem(getItem());
			player.sendMessage(ChatColor.GOLD + "You have been given the compass!");
			// Save Args
			savedArgs = args[0];
			player.sendMessage(ChatColor.GOLD + "Set tracking to " + args[0]);
		}
		return false;
	}
	
	public ItemStack getItem() {
		// Get a Compass
		ItemStack comp = new ItemStack(Material.COMPASS);
		ItemMeta meta = comp.getItemMeta();
		
		meta.setDisplayName("Player Compass");
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Tracking: " + savedArgs);
		lore.add(" ");
		lore.add("Compass made for Manhunts");
		lore.add("Developer: Leo Power");
		meta.setLore(lore);
		comp.setItemMeta(meta);
		return comp;
	}
	
	public void setCompassTarget(Player target, Player player) {
		// Set Compass Direction
		Location loc = target.getLocation();
		player.setCompassTarget(loc);
	}
	
	@EventHandler()
	public void onClick(PlayerInteractEvent event) {
		// On Click
		if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) {
			Player player = (Player) event.getPlayer();
			Player target = Bukkit.getPlayer(savedArgs);
			if (event.getAction() == Action.RIGHT_CLICK_AIR) {
				// Check if player has cooldown
				if (cooldowns.containsKey(player.getName())) {
					// Player is inside HashMap
					if (cooldowns.get(player.getName()) > System.currentTimeMillis()) {
						// still on cooldown
						long timeleft = (cooldowns.get(player.getName()) - System.currentTimeMillis() / 1000);
						player.sendMessage(ChatColor.RED + "Hook ready in " + timeleft + " second(s)");
						return;
					}
				}
				cooldowns.put(player.getName(), System.currentTimeMillis() + (10 * 1000));
				// Run setCompassTarget()
				setCompassTarget(target, player);
			}
		}
	}
}





















