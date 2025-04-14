package nl.multitime.multiWeb.tasks;

import nl.multitime.multiWeb.MultiWeb;
import org.bukkit.entity.Player;

public class NotificationTask implements Runnable {

    private final MultiWeb plugin;

    public NotificationTask(MultiWeb plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getConfig().getBoolean("notifications.enabled", true)) {
            return;
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("multiweb.bypass")) {
                continue;
            }

            plugin.getApi().getNotifications(player.getUniqueId()).thenAccept(response -> {
                if (!response.isSuccess() || response.getData() == null) {
                    return;
                }

                if (response.getData().has("notifications") && !response.getData().get("notifications").isJsonNull()) {
                    int count = response.getData().getAsJsonArray("notifications").size();

                    if (count > 0) {
                        player.sendMessage(plugin.formatMessage(plugin.getConfig().getString("messages.notifications")
                                .replace("{count}", String.valueOf(count))));
                    }
                }
            });
        }
    }
}
