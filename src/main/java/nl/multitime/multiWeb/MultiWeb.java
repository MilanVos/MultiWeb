package nl.multitime.multiWeb;

import nl.multitime.multiWeb.api.NamelessAPI;
import nl.multitime.multiWeb.commands.NamelessCommand;
import nl.multitime.multiWeb.commands.RegisterCommand;
import nl.multitime.multiWeb.commands.VerifyCommand;
import nl.multitime.multiWeb.listeners.PlayerListener;
import nl.multitime.multiWeb.tasks.NotificationTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MultiWeb extends JavaPlugin {

    private static MultiWeb instance;
    private String apiURL;
    private String apiKey;
    private NamelessAPI api;
    private BukkitTask notificationTask;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        loadConfig();

        api = new NamelessAPI(this);

        checkApiConnection();

        registerCommands();

        registerEvents();

        startNotificationTask();

        getLogger().info("MultiWeb NamelessMC integration has been enabled!");
    }

    @Override
    public void onDisable() {
        if (notificationTask != null) {
            notificationTask.cancel();
        }

        getLogger().info("MultiWeb NamelessMC integration has been disabled!");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        apiURL = config.getString("api.url", "");
        apiKey = config.getString("api.key", "");

        if (apiURL.isEmpty() || apiKey.isEmpty()) {
            getLogger().warning("API URL or API Key not set in config.yml!");
        }
    }

    private void registerCommands() {
        getCommand("nameless").setExecutor(new NamelessCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("verify").setExecutor(new VerifyCommand(this));
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void checkApiConnection() {
        api.checkConnection().thenAccept(response -> {
            if (response.isSuccess()) {
                getLogger().info("Successfully connected to NamelessMC API!");
            } else {
                getLogger().warning("Failed to connect to NamelessMC API: " + response.getMessage());
            }
        });
    }

    private void startNotificationTask() {
        int interval = getConfig().getInt("notifications.interval", 60);
        notificationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new NotificationTask(this), 20L * 10L, 20L * interval);
    }

    public void reloadPlugin() {
        if (notificationTask != null) {
            notificationTask.cancel();
        }

        reloadConfig();
        loadConfig();

        checkApiConnection();

        startNotificationTask();
    }

    public String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix", "&8[&bMultiWeb&8] ") + message);
    }

    public boolean hasCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) {
            return false;
        }

        long cooldownTime = getConfig().getLong("cooldown", 60) * 1000;
        long timePassed = System.currentTimeMillis() - cooldowns.get(uuid);

        if (timePassed >= cooldownTime) {
            cooldowns.remove(uuid);
            return false;
        }

        return true;
    }

    public void setCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    public long getRemainingCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) {
            return 0;
        }

        long cooldownTime = getConfig().getLong("cooldown", 60) * 1000;
        long timePassed = System.currentTimeMillis() - cooldowns.get(uuid);

        return Math.max(0, (cooldownTime - timePassed) / 1000);
    }

    public static MultiWeb getInstance() {
        return instance;
    }

    public String getApiURL() {
        return apiURL;
    }

    public String getApiKey() {
        return apiKey;
    }

    public NamelessAPI getApi() {
        return api;
    }
}
