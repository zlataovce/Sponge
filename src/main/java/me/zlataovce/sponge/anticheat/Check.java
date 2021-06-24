package me.zlataovce.sponge.anticheat;

import me.zlataovce.sponge.Main;
import me.zlataovce.sponge.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class Check implements Listener {
    private final Main plugin;

    public HashMap<UUID, Long> disableTime = new HashMap<>();
    public HashMap<UUID, Integer> vls = new HashMap<>();
    public HashMap<UUID, Integer> lastVLs = new HashMap<>();

    public Check(Main plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void increaseVL(UUID uuid, int amount) {
        if (!this.isEnabled()) {
            return;
        }
        if (!vls.containsKey(uuid)) {
            vls.put(uuid, amount);
        } else {
            lastVLs.put(uuid, vls.get(uuid));
            vls.put(uuid, vls.get(uuid) + amount);
        }
    }
    public int getVL(UUID uuid) {
        if (!vls.containsKey(uuid)) {
            vls.put(uuid, 0);
        }
        return vls.get(uuid);
    }
    public int getLastVL(UUID uuid) {
        if (!lastVLs.containsKey(uuid)) {
            lastVLs.put(uuid, 0);
        }
        return lastVLs.get(uuid);
    }

    public void decreaseVL(UUID uuid, int amount) {
        if (!vls.containsKey(uuid)) {
            vls.put(uuid, amount);
        } else if (vls.get(uuid) - amount >= 0) {
            lastVLs.put(uuid, vls.get(uuid));
            vls.put(uuid, vls.get(uuid) - amount);
        }
    }

    public void decreaseAllVL(int amount) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!vls.containsKey(uuid)) {
                vls.put(uuid, amount);
            } else if (vls.get(uuid) - amount >= 0) {
                lastVLs.put(uuid, vls.get(uuid));
                vls.put(uuid, vls.get(uuid) - amount);
            }
        }
    }

    public void resetVL(UUID uuid) {
        lastVLs.put(uuid, vls.get(uuid));
        vls.put(uuid, 0);
    }

    public boolean isDisabled(Player player) {
        if (!disableTime.containsKey(player.getUniqueId())) {
            disableTime.put(player.getUniqueId(), System.currentTimeMillis());
        }
        return disableTime.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    public boolean isEnabled() {
        return plugin.getMainConfig().node("anticheat").getBoolean();
    }

    public void logCheat(UUID uuid, String cheatType, int vl) {
        Player offender = Bukkit.getPlayer(uuid);
        if (offender == null) {
            return;
        }
        String formattedMessage = String.format(ChatUtils.color("&l&eSponge &r&l&4>>&r &c%1$s&r - %2$s &bVL: &r%3$s"), offender.getName(), cheatType, vl);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("sponge.alerts")) {
                player.sendMessage(formattedMessage);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) {
            return;
        }
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }
        Player player = (Player) event.getEntity();
        disableTime.put(player.getUniqueId(), System.currentTimeMillis() + 1500);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        disableTime.put(player.getUniqueId(), System.currentTimeMillis() + 1500);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        vls.remove(player.getUniqueId());
    }
}