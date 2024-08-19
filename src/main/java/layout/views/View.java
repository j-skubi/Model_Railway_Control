package layout.views;

import com.google.gson.JsonObject;

public abstract class View {
    protected boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public abstract JsonObject toClient();
    public abstract JsonObject save();
    public abstract JsonObject changeState(int viewID);
}
