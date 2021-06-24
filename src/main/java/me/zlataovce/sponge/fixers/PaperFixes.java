package me.zlataovce.sponge.fixers;

import me.zlataovce.sponge.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;


public class PaperFixes implements Listener {
    private final Main plugin;

    public PaperFixes(Main plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Loaded PaperFixes!");
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        Entity ent = e.getEntity();
        if (ent instanceof ArmorStand) {
            if (!plugin.getMainConfig().node("armorstand-gravity").getBoolean()) {
                ent.setGravity(false);
            }
        }
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        ArmorStand ent = e.getRightClicked();

        if (!plugin.getMainConfig().node("armorstand-gravity").getBoolean()) {
            ent.setGravity(false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPistonOut(BlockPistonExtendEvent e) {
        if (plugin.getMainConfig().node("disable-duplication").getBoolean()) {
            this.destroyExploitableBlocks(e.getBlocks());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPistonIn(BlockPistonRetractEvent e) {
        if (plugin.getMainConfig().node("disable-duplication").getBoolean()) {
            this.destroyExploitableBlocks(e.getBlocks());
        }
    }

    public boolean hasExploitableBlock(List<Block> blocks) {
        if (blocks.size() >= 3) {
            boolean containsTnt = false;
            boolean containsSlime = false;
            boolean containsHoney = false;
            boolean containsRail = false;

            for (Block block : blocks) {
                Material material = block.getType();
                if (material == Material.DETECTOR_RAIL) {
                    containsRail = true;
                } else if (material == Material.TNT) {
                    containsTnt = true;
                } else if (material == Material.SLIME_BLOCK) {
                    containsSlime = true;
                } else if (material == Material.HONEY_BLOCK) {
                    containsHoney = true;
                }
            }

            return ((containsTnt || containsRail) && (containsHoney || containsSlime));
        }
        return false;
    }

    public void destroyExploitableBlocks(List<Block> blocks) {
        if (this.hasExploitableBlock(blocks)) {
            plugin.getLogger().info("Possible dupe detected.");
            for (Block block : blocks) {
                Material material = block.getType();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if ((material == Material.DETECTOR_RAIL) || (material == Material.TNT)) {
                            block.setType(Material.AIR);
                            for (BlockFace face : BlockFace.values()) {
                                Block nearbyBlock = block.getRelative(face, 3);
                                Material nearbyBlockMaterial = nearbyBlock.getType();
                                if ((nearbyBlockMaterial == Material.DETECTOR_RAIL) || (nearbyBlockMaterial == Material.TNT)) {
                                    nearbyBlock.setType(Material.AIR);
                                }
                            }
                        }
                    }
                }.runTaskLater(plugin, 5L);
            }
        }
    }
}
