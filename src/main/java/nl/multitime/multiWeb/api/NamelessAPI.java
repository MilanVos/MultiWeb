package nl.multitime.multiWeb.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.multitime.multiWeb.MultiWeb;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class NamelessAPI {

    private final MultiWeb plugin;
    private final Gson gson;

    public NamelessAPI(MultiWeb plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
    }

    public CompletableFuture<APIResponse> registerUser(Player player, String email) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("username", player.getName());
                payload.addProperty("uuid", player.getUniqueId().toString());
                payload.addProperty("email", email);

                return makeRequest("/register", payload);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error registering user", e);
                return new APIResponse(false, "Er is een fout opgetreden bij het registreren.");
            }
        });
    }

    public CompletableFuture<APIResponse> verifyUser(Player player, String code) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("uuid", player.getUniqueId().toString());
                payload.addProperty("code", code);

                return makeRequest("/verify", payload);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error verifying user", e);
                return new APIResponse(false, "Er is een fout opgetreden bij het verifiëren.");
            }
        });
    }

    public CompletableFuture<Boolean> isUserRegistered(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("uuid", uuid.toString());

                APIResponse response = makeRequest("/userinfo", payload);
                return response.isSuccess();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error checking user registration", e);
                return false;
            }
        });
    }

    public CompletableFuture<APIResponse> getNotifications(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("uuid", uuid.toString());

                return makeRequest("/notifications", payload);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error getting notifications", e);
                return new APIResponse(false, "Er is een fout opgetreden bij het ophalen van notificaties.");
            }
        });
    }

    public CompletableFuture<APIResponse> checkConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return makeRequest("/info", new JsonObject());
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error checking API connection", e);
                return new APIResponse(false, "Kan geen verbinding maken met de API.");
            }
        });
    }

    private APIResponse makeRequest(String endpoint, JsonObject data) throws IOException {
        String apiUrl = plugin.getApiURL();
        if (!apiUrl.endsWith("/")) {
            apiUrl += "/";
        }

        URL url = new URL(apiUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + plugin.getApiKey());
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                boolean success = false;
                String message = "Unknown error";

                if (jsonResponse.has("success")) {
                    success = jsonResponse.get("success").getAsBoolean();
                }

                if (jsonResponse.has("message")) {
                    message = jsonResponse.get("message").getAsString();
                }

                return new APIResponse(success, message, jsonResponse);
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                return new APIResponse(false, "API Error: " + responseCode + " - " + response.toString());
            }
        }
    }
}
