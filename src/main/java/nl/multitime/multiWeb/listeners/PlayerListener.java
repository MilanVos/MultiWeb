package nl.multitime.multiWeb.listeners;

import nl.multitime.multiWeb.MultiWeb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final MultiWeb plugin;

    public PlayerListener(MultiWeb plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getConfig().getBoolean("join-check.enabled", true)) {
            return;
        }

        if (player.hasPermission("multiweb.bypass")) {
            return;
        }

        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            plugin.getApi().isUserRegistered(player.getUniqueId()).thenAccept(registered -> {
                if (!registered && plugin.getConfig().getBoolean("join-check.notify-unregistered", true)) {
                    player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.not-registered")));

                    if (plugin.getConfig().getBoolean("join-check.send-register-instructions", true)) {
                        player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.register-instructions")));
                    }
                }
            });
        }, 20L);
    }
}
