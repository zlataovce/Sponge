package me.zlataovce.sponge.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import me.zlataovce.sponge.Main;
import me.zlataovce.sponge.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class InspectorStopCommand {
    public InspectorStopCommand(Main plugin) {
        CommandManager<CommandSender> manager = plugin.getCommandManager();

        try {
            manager.command(
                    manager.commandBuilder("stopinspect", ArgumentDescription.of("Stops inspecting."))
                            .handler(context -> {
                                if (context.getSender() instanceof ConsoleCommandSender) {
                                    context.getSender().sendMessage(ChatUtils.color("[&e&lSponge&r&f] In-game only!"));
                                    return;
                                }
                                Player sender = (Player) context.getSender();
                                if (sender.hasPermission("sponge.inspect")) {
                                    if (plugin.getInspector().isInspector(sender)) {
                                        plugin.getInspector().removeInspector(sender);
                                        context.getSender().sendMessage(ChatUtils.color("[&e&lSponge&r&f] Inspect mode was toggled off."));
                                    }
                                } else {
                                    context.getSender().sendMessage(ChatUtils.color("[&e&lSponge&r&f] You don't have enough permissions!"));
                                }
                            })
            );
        } catch (Exception e) {
            plugin.getLogger().warning("Could not register InspectorCommand!");
            e.printStackTrace();
        }
    }
}

