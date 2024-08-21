package layout.views;

import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.model.LayoutComponent;
import utils.IDGenerator;
import utils.datastructures.AVLTree;

public abstract class View {
    protected boolean isActive = true;

    public boolean isActive() {
        return isActive;
    }

    public abstract JsonObject toClient();
    public abstract JsonObject save();
    public abstract JsonObject changeState(int viewID);
    public abstract JsonObject addViewComponent(JsonObject component, AVLTree<LayoutComponent> model, IDGenerator idGenerator) throws CorruptedSaveFile;
}
