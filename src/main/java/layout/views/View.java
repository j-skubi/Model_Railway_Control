package layout.views;

import com.google.gson.JsonObject;

public abstract class View {
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public abstract JsonObject toClient();
}
