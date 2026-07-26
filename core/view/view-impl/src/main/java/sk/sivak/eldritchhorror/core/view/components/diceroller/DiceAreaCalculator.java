package sk.sivak.eldritchhorror.core.view.components.diceroller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage.ACTUAL_DICE_SIZE;
import static sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage.DICE_SIZE;


public class DiceAreaCalculator {

    private final static float BORDER_SIDE = 50;
    private final static float BORDER_TOP = 100;
    private final static float BORDER_BOTTOM = 100;
    private final float AVAILABLE_WIDTH = ViewProperties.VIEWPORT_WIDTH - BORDER_SIDE*2;
    private final float AVAILABLE_HEIGHT = ViewProperties.VIEWPORT_HEIGHT - BORDER_TOP - BORDER_BOTTOM;

    private List<Rectangle> rollAreaList = new LinkedList<>();
    private List<Vector2> returnAreaList = new LinkedList<>();

    public void prepareRollArea(int totalDiceCount) {
        rollAreaList.clear();
        int rows = (int)Math.round(Math.sqrt(totalDiceCount));
        int columnsRemaning = totalDiceCount;
        for (int i=0; i<rows; i++) {
            int columnsInRow = Math.round(columnsRemaning / (float)(rows-i));
            columnsRemaning -= columnsInRow;
            buildRow(i, columnsInRow, rows);
        }
    }

    private void buildRow(int rowIndex, int columnsInRow, int totalRows) {
        for (int columnIndex=0; columnIndex < columnsInRow; columnIndex++) {
            float availableHeight = AVAILABLE_HEIGHT / (float) totalRows;
            float availableWidth = AVAILABLE_WIDTH / (float) columnsInRow;
            float y = BORDER_BOTTOM + (rowIndex * availableHeight);
            float x = BORDER_SIDE + (columnIndex * availableWidth);
            rollAreaList.add(new Rectangle(x,y,availableWidth, availableHeight));
        }
    }

    Rectangle getRollArea(int index) {
        return rollAreaList.get(index);
    }


    public void prepareReturnArea(int totalDiceCount) {
        returnAreaList.clear();
        float originX = InfoStage.getInvestigatorHud().getPrefWidth() + 5;

        for (int i = 0; i < totalDiceCount; i++) {
            returnAreaList.add(new Vector2((i*ACTUAL_DICE_SIZE) + originX, - (DICE_SIZE - ACTUAL_DICE_SIZE)/2f));
        }

    }

    public Vector2 getReturnArea(int index) {
        return returnAreaList.get(index);
    }

    public static void main(String[] args) {
        DiceAreaCalculator diceAreaCalculator = new DiceAreaCalculator();
        diceAreaCalculator.prepareRollArea(7);
        diceAreaCalculator.prepareReturnArea(7);
        /*
        for (int i = 0; i < 24; i++) {
            diceAreaCalculator.prepare(i);
        }
        */

    }
}
