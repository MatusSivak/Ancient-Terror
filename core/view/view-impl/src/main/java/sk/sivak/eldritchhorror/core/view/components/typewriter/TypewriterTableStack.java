package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Stack;

public class TypewriterTableStack {

    private Stack<TableData> tableDataStack = new Stack();

    public TableData push(TableData tableData) {
        return tableDataStack.push(tableData);
    }

    public TableData peek() {
        return tableDataStack.peek();
    }

    public TableData pop() {
        return tableDataStack.pop();
    }

    static class TableData {
        public TableData(Table table) {
            this.table = table;
            this.offsetY = 0;
        }

        private Table table;
        private float offsetY;
        private Vector2 tablePosition;

        public void setTablePosition(Vector2 tablePosition) {
            this.tablePosition = tablePosition;
        }

        public Vector2 getTablePosition() {
            return tablePosition;
        }

        public float getOffsetY() {
            return offsetY;
        }

        public Table getTable() {
            return table;
        }

        public void increaseOffsetY(float amount) {
            offsetY += amount;
        }
    }
}
