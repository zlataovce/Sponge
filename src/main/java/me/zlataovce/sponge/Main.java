package me.zlataovce.sponge;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.zlataovce.sponge.anticheat.InteractFrequency;
import me.zlataovce.sponge.commands.InspectorCommand;
import me.zlataovce.sponge.commands.InspectorStopCommand;
import me.zlataovce.sponge.commands.RulesCommand;
import me.zlataovce.sponge.commands.SpongeCommand;
import me.zlataovce.sponge.fixers.PaperFixes;
import me.zlataovce.sponge.inspector.Inspector;
import me.zlataovce.sponge.utils.TimeAnimate;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.function.Function;

public class Main extends JavaPlugin {
    @Getter
    CommandManager<CommandSender> commandManager;

    @Getter
    ConfigurationNode MainConfig;

    @Getter
    InteractFrequency interactFrequencyCheck;

    @Getter
    Inspector inspector;

    public void onEnable() {
        this.saveDefaultConfig();
        File config = new File(this.getDataFolder(), "config.yml");
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(config.toPath()).build();
        try {
            MainConfig = loader.load();
        } catch (ConfigurateException e) {
            this.getLogger().warning("Could not load config.yml!");
            e.printStackTrace();
        }
        try {
            commandManager = new BukkitCommandManager<>(
                    /* Owning plugin */ this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (Exception e) {
            this.getLogger().warning("Could not register BukkitCommandManager!");
            e.printStackTrace();
        }
        new TimeAnimate(this);
        interactFrequencyCheck = new InteractFrequency(this);
        inspector = new Inspector(this);
        new InspectorCommand(this);
        new InspectorStopCommand(this);
        new SpongeCommand(this);
        if (this.MainConfig.node("rules-command").getBoolean()) {
            new RulesCommand(this);
        }
        if (!PaperLib.isPaper() && this.MainConfig.node("paper-fixes").getBoolean()) {
            new PaperFixes(this);
        }
    }
}
