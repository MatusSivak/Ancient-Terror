package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;

import java.util.List;

/**
 * @author msivak
 */
public class SelectLocationDialog extends Dialog {

    private SingleSubscriber<? super LocationId> singleSubscriber;

    public SelectLocationDialog(String title, Skin skin) {
        super(title, skin);
    }

    public void init(List<LocationInfo.Connection> connections) {
        for (LocationInfo.Connection connection : connections) {
            TextButton button = new TextButton(connection.getLocationId().name(), getSkin());
            getContentTable()
                    .add(button)
                    .row();
            button.addListener(new ButtonListener(connection));
        }
    }

    public void setSingleSubscriber(SingleSubscriber<? super LocationId> singleSubscriber) {
        this.singleSubscriber = singleSubscriber;
    }

    private class ButtonListener extends ClickListener {

        private LocationInfo.Connection connection;

        public ButtonListener(LocationInfo.Connection connection) {
            this.connection = connection;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            singleSubscriber.onSuccess(connection.getLocationId());
            hide();
        }
    }
}
