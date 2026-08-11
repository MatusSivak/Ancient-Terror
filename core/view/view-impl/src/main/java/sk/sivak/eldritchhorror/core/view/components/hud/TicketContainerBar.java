package sk.sivak.eldritchhorror.core.view.components.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_TRAVEL_TICKETS;

/**
 * @author msivak
 */
public class TicketContainerBar extends ContainerBar {

    private PathType[] slots = new PathType[MAX_TRAVEL_TICKETS];

    public TicketContainerBar() {
        super(CustomAssetManager.getTexture(CustomAssetManager.TICKET_BLANK));
    }

    public void addTicket(PathType pathType) {
        Cell cell = getCells().get(currentValue);
        Texture texture = CustomAssetManager.getTexture(
                pathType == PathType.SHIP ? CustomAssetManager.TICKET_SHIP_DOWN : CustomAssetManager.TICKET_TRAIN_DOWN);
        cell.setActor(new Image(texture));
        slots[currentValue] = pathType;
        currentValue++;
    }

    public void clearTickets() {
        clearChildren();
        currentValue = 0;
        setTotalEmptyContainers(MAX_TRAVEL_TICKETS);
    }

    @Override
    protected void addCell() {
        add().row();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        for (int i = 0; i < totalEmptyContainers; i++) {
            Cell cell = getCells().get(i);
            Image image = (Image) cell.getActor();

            float heightScale = getHeight() / image.getHeight() * 1 / (float) totalEmptyContainers;
            cell.width(image.getWidth() * heightScale);
            cell.height(image.getHeight() * heightScale);
        }
    }

    public Vector2 getTicketPosition(PathType input) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != input) {
                continue;
            }
            Image image = (Image) getCells().get(i).getActor();
            return image.localToStageCoordinates(new Vector2(0, 0));
        }
        throw new IllegalArgumentException();
    }

    public void removeTicket(PathType input) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != input) {
                continue;
            }
            Cell cell = getCells().get(i);
            Image image = new Image(CustomAssetManager.getTexture(CustomAssetManager.TICKET_BLANK));
            darkenContainer(image);
            cell.setActor(image);
            slots[i] = null;
            currentValue = i;
            break;
        }
    }


}
