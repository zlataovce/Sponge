package me.zlataovce.sponge.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.CommandManager;
import me.zlataovce.sponge.Main;
import me.zlataovce.sponge.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.stream.Collectors;

public class RulesCommand {
    public RulesCommand(Main plugin) {
        CommandManager<CommandSender> manager = plugin.getCommandManager();

        try {
            manager.command(
                    manager.commandBuilder("rules", ArgumentDescription.of("Gives the server's rules."))
                            .handler(context -> plugin.getMainConfig().node("rules").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList()).forEach(line -> context.getSender().sendMessage(ChatUtils.color(line))))
            );
        } catch (Exception e) {
            plugin.getLogger().warning("Could not register RulesCommand!");
            e.printStackTrace();
        }
    }
}

