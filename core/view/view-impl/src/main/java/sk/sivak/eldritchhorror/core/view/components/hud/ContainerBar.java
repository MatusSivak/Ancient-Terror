package sk.sivak.eldritchhorror.core.view.components.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

/**
 * @author msivak
 */
public class ContainerBar extends Table {

    private final Texture texture;
    protected int totalEmptyContainers;
    protected int currentValue;
    private String id;
    private boolean displayBlackContainers = false;
    private RepeatAction highlightAction;

    public ContainerBar(Texture texture) {
        this.texture = texture;
        align(Align.left);
    }

    public ContainerBar(Texture texture, String id) {
        this(texture);
        this.id = id;
    }

    protected void addCell() {
        add();
    }

    public void setTotalEmptyContainers(int totalEmptyContainers) {
        this.totalEmptyContainers = totalEmptyContainers;
        displayBlackContainers = true;
        initWithEmptyContainers(totalEmptyContainers);
    }

    private void initWithEmptyContainers(int totalEmptyContainers) {
        clearChildren();
        if (totalEmptyContainers == 0) {
            add().height(getHeight());
        }
        for (int i = 0; i < totalEmptyContainers; i++) {
            addCell();
            Cell cell = getCells().get(i);
            Image image = new Image(texture);
            darkenContainer(image);
            cell.setActor(image);
            cell.height(getHeight());
            cell.width(getHeight());
        }
    }

    protected void darkenContainer(Image image) {
        image.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (totalEmptyContainers == 0) {
            add().height(getHeight()).width(getHeight());
        }
        for (int i = 0; i < totalEmptyContainers; i++) {
            Cell cell = getCells().get(i);
            Image image = (Image) cell.getActor();

            float heightScale = getHeight() / image.getHeight();
            cell.width(image.getWidth() * heightScale);
            cell.height(image.getHeight() * heightScale);
        }

        if (totalEmptyContainers > 0) {
            setWidth(getCells().get(0).getPrefWidth() * 8);
        }
    }

    public void increaseCurrentValue() {
        setCurrentValue(currentValue + 1);
    }


    public void decreaseCurrentValue() {
        setCurrentValue(currentValue - 1);
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        if (!displayBlackContainers) {
            initWithEmptyContainers(currentValue);
            this.totalEmptyContainers = currentValue;
        }

        for (int i = 0; i < totalEmptyContainers; i++) {
            darkenContainer((Image) getCells().get(i).getActor());
        }
        for (int i = 0; i < currentValue; i++) {
            Image image = (Image) getCells().get(i).getActor();
            image.setColor(Color.WHITE);
        }
    }

    public void highlightFullContainer() {
        if (currentValue == 0) {
            return; // because when I can reroll with clue (by using focus when I have tome of secrets), but don't have clue
        }
        Image image = (Image) getCells().get(currentValue - 1).getActor();
        highlightAction = Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                Actions.color(Color.DARK_GRAY, ViewProperties.FADING_EFFECT_DURATION),
                Actions.color(Color.WHITE, ViewProperties.FADING_EFFECT_DURATION)
        ));
        image.addAction(highlightAction);
    }

    public void stopHighlight() {
        if (currentValue == 0) {
            return;
        }
        Image image = (Image) getCells().get(currentValue - 1).getActor();
        image.removeAction(highlightAction);
        image.setColor(Color.WHITE);
    }

    public Vector2 getEmptyContainerPosition() {
        return getEmptyContainerPosition(0);
    }

    public Vector2 getEmptyContainerPosition(int offset) {
        if (getCells().size <= currentValue + offset) {
            Cell cell = getCells().get(getCells().size - 1);
            float prefWidth = cell.getPrefWidth();
            return cell.getActor().localToStageCoordinates(new Vector2(prefWidth, 0));
        }
        Image image = (Image) getCells().get(currentValue + offset).getActor();
        if (image == null) {
            return localToStageCoordinates(new Vector2(0, 0));
        }
        return image.localToStageCoordinates(new Vector2(0, 0));
    }

    public Vector2 getFullContainerPosition() {
        return getFullContainerPosition(0);
    }

    public Vector2 getFullContainerPosition(int offset) {
        Image image = (Image) getCells().get(currentValue - 1 - offset).getActor();
        return image.localToStageCoordinates(new Vector2(0, 0));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

}
