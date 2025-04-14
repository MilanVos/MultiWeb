package nl.multitime.multiWeb.commands;

import nl.multitime.multiWeb.MultiWeb;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VerifyCommand implements CommandExecutor {

    private final MultiWeb plugin;

    public VerifyCommand(MultiWeb plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("multiweb.verify")) {
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
            player.sendMessage(ChatColor.RED + "Usage: /verify <code>");
            return false;
        }

        String code = args[0];

        plugin.getApi().verifyUser(player, code).thenAccept(response -> {
            if (response.isSuccess()) {
                player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.verify-success")));

                if (plugin.getConfig().getBoolean("verification.execute-commands", false)) {
                    for (String cmd : plugin.getConfig().getStringList("verification.commands")) {
                        plugin.getServer().dispatchCommand(
                                plugin.getServer().getConsoleSender(),
                                cmd.replace("{player}", player.getName())
                                   .replace("{uuid}", player.getUniqueId().toString())
                        );
                    }
                }
            } else {
                player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.verify-error")
                        .replace("{error}", response.getMessage())));
            }
        });

        plugin.setCooldown(player.getUniqueId());

        return true;
    }
}
