package me.zlataovce.sponge.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import me.zlataovce.sponge.Main;
import me.zlataovce.sponge.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class InspectorCommand {
    public InspectorCommand(Main plugin) {
        CommandManager<CommandSender> manager = plugin.getCommandManager();

        try {
            manager.command(
                    manager.commandBuilder("spongeinspect", ArgumentDescription.of("Inspects a given player."))
                            .argument(PlayerArgument.of("player"))
                            .handler(context -> {
                                if (context.getSender() instanceof ConsoleCommandSender) {
                                    context.getSender().sendMessage(ChatUtils.color("[&e&lSponge&r&f] In-game only!"));
                                    return;
                                }
                                Player sender = (Player) context.getSender();
                                Player target = context.get("player");
                                if (target == sender) {
                                    context.getSender().sendMessage(ChatUtils.color("[&e&lSponge&r&f] You can't inspect yourself!"));
                                    return;
                                }
                                if (context.getSender().hasPermission("sponge.inspect")) {
                                    if (plugin.getInspector().isInspector(sender)) {
                                        context.getSender().sendMessage(ChatUtils.color("[&e&lSponge&r&f] You are already inspecting someone!"));
                                        return;
                                    }
                                    plugin.getInspector().addInspector(sender, target.getLocation());
                                    context.getSender().sendMessage(ChatUtils.color("[&e&lSponge&r&f] Now inspecting player &c" + target.getName() + "&r."));
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

