package layout;

import layout.model.LayoutComponent;
import layout.views.ViewHandler;
import utils.IDGenerator;
import utils.datastructures.AVLTree;


public class Layout {
    private final IDGenerator idGenerator;
    private final ViewHandler viewHandler;
    private final AVLTree<LayoutComponent> components;



    public Layout() {
        idGenerator = new IDGenerator(Integer.MIN_VALUE);
        viewHandler = new ViewHandler();
        components = new AVLTree<>();
    }
}
