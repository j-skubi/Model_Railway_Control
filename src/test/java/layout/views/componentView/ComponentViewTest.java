package layout.views.componentView;

import com.google.gson.JsonParser;
import exceptions.CorruptedSaveFile;
import layout.JsonSaveFileStrings;
import layout.model.LayoutComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.datastructures.AVLTree;


class ComponentViewTest {

    AVLTree<LayoutComponent> model;

    @BeforeEach
    public void setup() {
        model = new AVLTree<>();
        model.insert(LayoutComponent.fromJson(JsonParser.parseString(JsonSaveFileStrings.TestTurnout).getAsJsonObject()));

    }
    @Test
    void save() throws CorruptedSaveFile {
        ComponentView componentView = new ComponentView(JsonParser.parseString(JsonSaveFileStrings.TestComponentViews).getAsJsonObject(), model);
        Assertions.assertEquals(componentView.save(),JsonParser.parseString(JsonSaveFileStrings.TestComponentViews).getAsJsonObject());
    }
}