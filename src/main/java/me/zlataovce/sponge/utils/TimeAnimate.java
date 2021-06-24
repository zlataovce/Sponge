package me.zlataovce.sponge.utils;

import me.zlataovce.sponge.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeAnimate implements Listener {
    private final Main plugin;
    private boolean skipping;

    public TimeAnimate(Main plugin) {
        this.plugin = plugin;
        this.skipping = false;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        event.setCancelled(true);
        World world = event.getBed().getWorld();

        if (world.hasStorm()) {
            world.setStorm(false);
            for (Player receiver : Bukkit.getOnlinePlayers()) {
                receiver.sendMessage(ChatUtils.color("[&e&lSponge&r&f] The weather has been cleared."));
            }
            return;
        }
        if (!this.skipping) {
            for (Player receiver : Bukkit.getOnlinePlayers()) {
                receiver.sendMessage(ChatUtils.color("[&e&lSponge&r&f] Skipping the night..."));
            }
            this.accelerate(world);
        }
    }

    public void accelerate(World world) {
        skipping = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                world.setTime(world.getTime() + 80);
                if (!(world.getTime() < 23460 && world.getTime() > 12542)) {
                    skipping = false;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }
}
