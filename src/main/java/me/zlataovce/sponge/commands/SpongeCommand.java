package me.zlataovce.sponge.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.CommandManager;
import me.zlataovce.sponge.Main;
import me.zlataovce.sponge.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SpongeCommand {
    public SpongeCommand(Main plugin) {
        CommandManager<CommandSender> manager = plugin.getCommandManager();

        try {
            manager.command(
                    manager.commandBuilder("sponge", ArgumentDescription.of("Gives useful information about the server."))
                            .handler(context -> {
                                Runtime r = Runtime.getRuntime();
                                long memUsed = (r.maxMemory() - r.freeMemory()) / 1048576;
                                context.getSender().sendMessage(ChatUtils.color(String.format("&ePlayers:&r &b%1$s &r/ &4%2$s", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers())));
                                context.getSender().sendMessage(ChatUtils.color(String.format("&eRAM:&r &b%1$s &r/ &4%2$s MB", memUsed, r.maxMemory() / 1048576)));
                            })
            );
        } catch (Exception e) {
            plugin.getLogger().warning("Could not register SpongeCommand!");
            e.printStackTrace();
        }
    }
}

