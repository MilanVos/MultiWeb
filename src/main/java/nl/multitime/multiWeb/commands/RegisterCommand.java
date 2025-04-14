package nl.multitime.multiWeb.commands;

import nl.multitime.multiWeb.MultiWeb;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class RegisterCommand implements CommandExecutor {

    private final MultiWeb plugin;
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public RegisterCommand(MultiWeb plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("multiweb.register")) {
            player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (plugin.hasCooldown(player.getUniqueId())) {
            long remaining = plugin.getRemainingCooldown(player.getUniqueId());
            player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.cooldown")
                    .replace("{seconds}", String.valueOf(remaining))));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /register <email>");
            return false;
        }

        String email = args[0];

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.invalid-email")));
            return true;
        }

        plugin.getApi().isUserRegistered(player.getUniqueId()).thenAccept(registered -> {
            if (registered) {
                player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.register-already")));
                return;
            }

            plugin.getApi().registerUser(player, email).thenAccept(response -> {
                if (response.isSuccess()) {
                    player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.register-success")));

                    if (plugin.getConfig().getBoolean("registration.send-verification-instructions", true)) {
                        player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.verification-instructions")));
                    }
                } else {
                    player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.register-error")
                            .replace("{error}", response.getMessage())));
                }
            });

            plugin.setCooldown(player.getUniqueId());
        });

        return true;
    }
}
