package nl.multitime.multiWeb.commands;

import nl.multitime.multiWeb.MultiWeb;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NamelessCommand implements CommandExecutor, TabCompleter {

    private final MultiWeb plugin;
    private final List<String> subCommands = Arrays.asList("reload", "status", "help");

    public NamelessCommand(MultiWeb plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("multiweb.admin")) {
            sender.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "MultiWeb configuration reloaded!");
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            sender.sendMessage(ChatColor.GRAY + "Checking API connection...");

            plugin.getApi().checkConnection().thenAccept(response -> {
                if (response.isSuccess()) {
                    sender.sendMessage(ChatColor.GRAY + "==== " + ChatColor.AQUA + "MultiWeb Status" + ChatColor.GRAY + " ====");
                    sender.sendMessage(ChatColor.YELLOW + "API URL: " + ChatColor.GREEN + plugin.getApiURL());
                    sender.sendMessage(ChatColor.YELLOW + "API Status: " + ChatColor.GREEN + "Connected");
                    sender.sendMessage(ChatColor.YELLOW + "API Version: " + ChatColor.GREEN +
                            (response.getData() != null && response.getData().has("version") ?
                                    response.getData().get("version").getAsString() : "Unknown"));
                } else {
                    sender.sendMessage(ChatColor.GRAY + "==== " + ChatColor.AQUA + "MultiWeb Status" + ChatColor.GRAY + " ====");
                    sender.sendMessage(ChatColor.YELLOW + "API URL: " + ChatColor.RED + plugin.getApiURL());
                    sender.sendMessage(ChatColor.YELLOW + "API Status: " + ChatColor.RED + "Disconnected");
                    sender.sendMessage(ChatColor.YELLOW + "Error: " + ChatColor.RED + response.getMessage());
                }
            });

            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /nameless help for help.");
        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "==== " + ChatColor.AQUA + "MultiWeb NamelessMC" + ChatColor.GRAY + " ====");
        sender.sendMessage(ChatColor.YELLOW + "/nameless help" + ChatColor.GRAY + " - Show this help message");
        sender.sendMessage(ChatColor.YELLOW + "/nameless reload" + ChatColor.GRAY + " - Reload the configuration");
        sender.sendMessage(ChatColor.YELLOW + "/nameless status" + ChatColor.GRAY + " - Check API connection status");
        sender.sendMessage(ChatColor.YELLOW + "/register <email>" + ChatColor.GRAY + " - Register on the website");
        sender.sendMessage(ChatColor.YELLOW + "/verify <code>" + ChatColor.GRAY + " - Verify your account");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
