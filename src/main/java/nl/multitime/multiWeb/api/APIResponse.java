package nl.multitime.multiWeb.api;

import com.google.gson.JsonObject;

public class APIResponse {
    private final boolean success;
    private final String message;
    private final JsonObject data;

    public APIResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public APIResponse(boolean success, String message, JsonObject data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public JsonObject getData() {
        return data;
    }
}
