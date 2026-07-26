package sk.sivak.eldritchhorror.core.view.components.select;

import com.badlogic.gdx.graphics.Color;
import java8.features.function.Function;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.components.button.HideOkButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class SelectSingleComponent<Key> {
    private List<Key> availableKeys;
    private SelectComponentsComplexTable<Key> selectComponentsComplexTable;
    private SingleSubscriber<? super Key> subscriber;
    private HideOkButtons hideOkButtons;
    private String hideText;

    public SelectSingleComponent(List<Key> availableKeys,
                                 SingleSubscriber<? super Key> subscriber) {
        this.availableKeys = availableKeys;
        this.subscriber = subscriber;
        hideOkButtons = new HideOkButtons();
        selectComponentsComplexTable = new SelectComponentsComplexTable<Key>() {
            @Override
            protected void hideOkButton() {
                hideOkButtons.disableOkButton();
            }

            @Override
            protected void showOkButton() {
                hideOkButtons.enableOkButton();
            }
        };
    }

    public void init(String hideText, String title, Function<Float, Color> backgroundColorFn, String backgroundTexture) {
        this.hideText = hideText;
        this.selectComponentsComplexTable.setTitle(title);
        this.selectComponentsComplexTable.setBackgroundColorFn(backgroundColorFn);
        this.selectComponentsComplexTable.setBackgroundTexture(backgroundTexture);
    }

    public void show() {

        selectComponentsComplexTable.init(availableKeys);
        selectComponentsComplexTable.setPosition(
                (VIEWPORT_WIDTH-75) / 2 - selectComponentsComplexTable.getWidth() / 2 + 75,
                160);
        InfoStage.showActor(selectComponentsComplexTable);

        hideOkButtons.init(hideText, getOnConfirmAction(), selectComponentsComplexTable);
        hideOkButtons.showButtons();
        hideOkButtons.disableOkButton();
    }

    private Action0 getOnConfirmAction() {
        return () -> onSelect(selectComponentsComplexTable.getSelectedKey());
    }


    private void onSelect(Key key) {
        selectComponentsComplexTable.disable(key);
        InfoStage.hideActor(selectComponentsComplexTable, () -> subscriber.onSuccess(key), 0.25f);
    }
}
