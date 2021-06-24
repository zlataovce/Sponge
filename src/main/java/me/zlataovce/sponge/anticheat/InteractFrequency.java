package me.zlataovce.sponge.anticheat;

import me.zlataovce.sponge.Main;
import me.zlataovce.sponge.utils.ChatUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class InteractFrequency extends Check implements Listener {
    private final Main plugin;

    public InteractFrequency(Main plugin) {
        super(plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                checkForAlert();
            }
        }.runTaskTimer(plugin, 0L, 10L);
        new BukkitRunnable() {
            @Override
            public void run() {
                decreaseAllVL(1);
            }
        }.runTaskTimer(plugin, 0L, 20L * 3);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!clicks.containsKey(player)) {
                        clicks.put(player, 0);
                    }
                    if (clicks.get(player) >= plugin.getMainConfig().node("clickspeed").getInt()) {
                        if (!isEnabled()) {
                            resetVL(player.getUniqueId());
                            return;
                        }
                        increaseVL(player.getUniqueId(), 3);
                        logCheat(player.getUniqueId(), String.format(ChatUtils.color("&5InteractFrequency &r(CPS: %1$s)"), clicks.get(player)), getVL(player.getUniqueId()));
                    }
                    clicks.put(player, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
        plugin.getLogger().info("Loaded InteractFrequency!");
    }

    private void checkForAlert() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!isEnabled()) {
                resetVL(uuid);
                return;
            }
        }
    }

    /**
     * Click Speed Check
     */

    private HashMap<Player, Integer> clicks = new HashMap<>();

    @EventHandler
    public void speedCheck(PlayerInteractEvent event) {
        if (!event.getPlayer().getType().equals(EntityType.PLAYER)) {
            return;
        }
        Player player = event.getPlayer();

        if (!event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            return;
        }

        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack.getType().equals(Material.FISHING_ROD)) {
            return;
        }

        if (!clicks.containsKey(player)) {
            clicks.put(player, 0);
        }
        clicks.put(player, clicks.get(player) + 1);
        if (clicks.get(player) >= plugin.getMainConfig().node("clickspeed").getInt()) {
            if (plugin.getMainConfig().node("cancelclick").getBoolean()) {
                event.setCancelled(true);
            }
        }
    }
}