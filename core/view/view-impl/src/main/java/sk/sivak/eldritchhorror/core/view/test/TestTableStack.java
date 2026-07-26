package sk.sivak.eldritchhorror.core.view.test;

import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Stack;

public class TestTableStack {

    private Stack<VisTable> stack = new Stack<>();

    public VisTable push(VisTable testTable) {
        return stack.push(testTable);
    }

    public VisTable peek() {
        return stack.peek();
    }

    public VisTable pop() {
        return stack.pop();
    }
}
