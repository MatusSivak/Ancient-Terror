package sk.sivak.eldritchhorror.core.view.components.diceroller;

import java.util.Stack;

public class DiceRollerStack {

    private Stack<DiceRoller> stack = new Stack<>();

    public DiceRoller push() {
        return stack.push(new DiceRoller());
    }

    public DiceRoller peek() {
        return stack.peek();
    }

    public DiceRoller pop() {
        return stack.pop();
    }
}
