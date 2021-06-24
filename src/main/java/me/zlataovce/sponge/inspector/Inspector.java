package me.zlataovce.sponge.inspector;

import io.papermc.lib.PaperLib;
import me.zlataovce.sponge.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class Inspector implements Listener {
    private final Main plugin;

    private HashMap<Player, Location> inspectors = new HashMap<>();

    public Inspector(Main plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean isInspector(Player player) {
        return this.inspectors.containsKey(player);
    }

    public void addInspector(Player player, Location location) {
        this.inspectors.put(player, player.getLocation());
        player.setGameMode(GameMode.SPECTATOR);
        PaperLib.teleportAsync(player, location);
        // player.teleport(location);
    }

    public void removeInspector(Player player) {
        PaperLib.teleportAsync(player, this.inspectors.get(player));
        // player.teleport(this.inspectors.get(player));
        player.setGameMode(GameMode.SURVIVAL);
        this.inspectors.remove(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (this.isInspector(e.getPlayer())) {
            this.removeInspector(e.getPlayer());
        }
    }
}
