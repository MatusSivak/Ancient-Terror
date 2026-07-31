package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

import java.util.List;

import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class ConfirmGateSpawnDialog extends Dialog {

    private CompletableSubscriber completableSubscriber;

    public ConfirmGateSpawnDialog(String title, Skin skin) {
        super(title, skin);

    }

    public void init(LocationId locationId, List<MonsterInfo> monsters) {
        getContentTable().clear();
        Label gateLabel = new Label(locationId.name(), getSkin());
        TextButton button = new TextButton(get("dialog.ok"), getSkin());
        button.addListener(new ButtonListener());
        getContentTable().add(gateLabel).colspan(monsters.size()).row();
        for (MonsterInfo monster : monsters) {
            getContentTable().add(new Label(monster.getName() + ", ", getSkin()));
        }
        getContentTable().row();
        getContentTable().add(button).colspan(monsters.size()).row();
    }

    public void setCompletableSubscriber(CompletableSubscriber completableSubscriber) {
        this.completableSubscriber = completableSubscriber;
    }

    private class ButtonListener extends ClickListener {

        @Override
        public void clicked(InputEvent event, float x, float y) {
            hide();
            completableSubscriber.onCompleted();
        }
    }
}
